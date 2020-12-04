package com.star.pibbledev.wallet.home.samsung.util;

import org.spongycastle.asn1.sec.SECObjectIdentifiers;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.asn1.x9.X9IntegerConverter;
import org.spongycastle.crypto.ec.CustomNamedCurves;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.custom.sec.SecP256K1Curve;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Uint;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.web3j.utils.Assertions.verifyPrecondition;

public class ETHUtil {

    private static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByOID(SECObjectIdentifiers.secp256k1);
    private static final ECDomainParameters CURVE;

    static {
        if(CURVE_PARAMS != null){
            CURVE = new ECDomainParameters(CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
        } else {
            CURVE = null;
        }
    }

    static public byte[] HDPathFromString(String hdpath) {
        boolean regex = hdpath.matches("([mM]/)([0-9]+'?/)*([0-9]+'?)");
        boolean depth = hdpath.split("/").length < 7;
        if (regex && depth) {
            return hdpath.getBytes();
        }
        return null;
    }

    static final String MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    public static byte[] getPrefixedMessage(byte[] message) {
        byte[] prefix = MESSAGE_PREFIX.concat(String.valueOf(message.length)).getBytes();

        byte[] result = new byte[prefix.length + message.length];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(message, 0, result, prefix.length, message.length);

        return result;

    }

    public static byte[] getSignedMessage(byte[] signatureRS, byte[] xPub, byte[] unsignedRawMessage) throws Exception {
        if (signatureRS != null && signatureRS.length == 64
                && unsignedRawMessage != null && unsignedRawMessage.length > 0
                && xPub != null && xPub.length == 65) {

            byte[] byteR = new byte[32];
            byte[] byteS = new byte[32];
            byte[] result = new byte[65];
            System.arraycopy(signatureRS, 0, byteR, 0, 32);
            System.arraycopy(signatureRS, 32, byteS, 0, 32);

//            CWLog.d(TAG, "SIGCHECK Result R : " + HexUtil.toHexString(byteR));
//            CWLog.d(TAG, "SIGCHECK Result S : " + HexUtil.toHexString(byteS));
//            CWLog.d(TAG, Numeric.toHexString(unsignedRawMessage));
            BigInteger r = new BigInteger(1, byteR);
            BigInteger s = new BigInteger(1, byteS);

            ECDSASignature signature = new ECDSASignature(r, s).toCanonicalised();

            byte[] compressedPubKey = Arrays.copyOfRange(xPub, 0, 33);
            byte[] decompressedKey = ETHUtil.decompressCompressedKey(compressedPubKey);
            byte[] messageHash = Hash.sha3(unsignedRawMessage);

            BigInteger pubkey = new BigInteger(1, decompressedKey);

            Sign.SignatureData data = ETHUtil.getSignatureDataFromECDSASignature(pubkey, signature, messageHash);
//            CWLog.d(TAG, "R: " + HexUtil.toHexString(data.getR()));
//            CWLog.d(TAG, "S: " + HexUtil.toHexString(data.getS()));
//            CWLog.d(TAG, "v: " + data.getV());
            System.arraycopy(signatureRS, 0, result, 0, 64);
            result[64] = data.getV();
            return result;
        }
        return null;
    }

    public static Sign.SignatureData getSignatureDataFromECDSASignature(BigInteger publicKey, ECDSASignature sig, byte[] messageHash) {

        // Now we have to work backwards to figure out the recId needed to recover the signature.
        int recId = -1;
        for (int i = 0; i < 4; i++) {
            BigInteger k = recoverFromSignature(i, sig, messageHash);
            if (k != null && k.equals(publicKey)) {
                recId = i;
                break;
            }
        }
        if (recId == -1) {
            throw new RuntimeException(
                    "Could not construct a recoverable key. This should never happen.");
        }

        int headerByte = recId + 27;

        // 1 header + 32 bytes for R + 32 bytes for S
        byte v = (byte) headerByte;
        byte[] r = Numeric.toBytesPadded(sig.r, 32);
        byte[] s = Numeric.toBytesPadded(sig.s, 32);

        return new Sign.SignatureData(v, r, s);
    }

    private static BigInteger recoverFromSignature(int recId, ECDSASignature sig, byte[] message) {
        if(CURVE == null){
            return null;
        }
        verifyPrecondition(recId >= 0, "recId must be positive");
        verifyPrecondition(sig.r.signum() >= 0, "r must be positive");
        verifyPrecondition(sig.s.signum() >= 0, "s must be positive");
        verifyPrecondition(message != null, "message cannot be null");

        // 1.0 For j from 0 to h   (h == recId here and the loop is outside this function)
        //   1.1 Let x = r + jn
        BigInteger n = CURVE.getN();  // Curve order.
        BigInteger i = BigInteger.valueOf((long) recId / 2);
        BigInteger x = sig.r.add(i.multiply(n));
        //   1.2. Convert the integer x to an octet string X of length mlen using the conversion
        //        routine specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or mlen = ⌈m/8⌉.
        //   1.3. Convert the octet string (16 set binary digits)||X to an elliptic curve point R
        //        using the conversion routine specified in Section 2.3.4. If this conversion
        //        routine outputs "invalid", then do another iteration of Step 1.
        //
        // More concisely, what these points mean is to use X as a compressed public key.
        BigInteger prime = SecP256K1Curve.q;
        if (x.compareTo(prime) >= 0) {
            // Cannot have point co-ordinates larger than this as everything takes place modulo Q.
            return null;
        }
        // Compressed keys require you to know an extra bit of data about the y-coord as there are
        // two possibilities. So it's encoded in the recId.
        ECPoint R = decompressKey(x, (recId & 1) == 1);
        if(R == null) {
            return null;
        }
        //   1.4. If nR != point at infinity, then do another iteration of Step 1 (callers
        //        responsibility).
        if (!R.multiply(n).isInfinity()) {
            return null;
        }
        //   1.5. Compute e from M using Steps 2 and 3 of ECDSA signature verification.
        BigInteger e = new BigInteger(1, message);
        //   1.6. For k from 1 to 2 do the following.   (loop is outside this function via
        //        iterating recId)
        //   1.6.1. Compute a candidate public key as:
        //               Q = mi(r) * (sR - eG)
        //
        // Where mi(x) is the modular multiplicative inverse. We transform this into the following:
        //               Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
        // Where -e is the modular additive inverse of e, that is z such that z + e = 0 (mod n).
        // In the above equation ** is point multiplication and + is point addition (the EC group
        // operator).
        //
        // We can find the additive inverse by subtracting e from zero then taking the mod. For
        // example the additive inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and
        // -3 mod 11 = 8.
        BigInteger eInv = BigInteger.ZERO.subtract(e).mod(n);
        BigInteger rInv = sig.r.modInverse(n);
        BigInteger srInv = rInv.multiply(sig.s).mod(n);
        BigInteger eInvrInv = rInv.multiply(eInv).mod(n);
        ECPoint q = ECAlgorithms.sumOfTwoMultiplies(CURVE.getG(), eInvrInv, R, srInv);

        byte[] qBytes = q.getEncoded(false);
        // We remove the prefix
        return new BigInteger(1, Arrays.copyOfRange(qBytes, 1, qBytes.length));
    }

    /**
     * Decompress a compressed public key (x co-ord and low-bit of y-coord).
     */
    private static ECPoint decompressKey(BigInteger xBN, boolean yBit) {
        if(CURVE == null){
            return null;
        }
        X9IntegerConverter x9 = new X9IntegerConverter();
        byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.getCurve()));
        compEnc[0] = (byte) (yBit ? 0x03 : 0x02);
        return CURVE.getCurve().decodePoint(compEnc);
    }

    public static byte[] decompressCompressedKey(byte[] compressedKey) {
        if(CURVE == null){
            return null;
        }
        ECPoint point = CURVE.getCurve().decodePoint(compressedKey);
        byte[] decompressed = new byte[64];
        // remove 0x4
        System.arraycopy(point.getEncoded(false), 1, decompressed, 0, 64);
        return decompressed;
    }

    public static Function createEthTransferData(String address, BigInteger tokenAmount) {
        return new Function("transfer"
                , Arrays.asList(new Address(address), new Uint(tokenAmount))
                , singletonList(new TypeReference<Uint>() {
        }));
    }
}
