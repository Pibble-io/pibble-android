package com.star.pibbledev.chatroom.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.star.pibbledev.BaseActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.chatroom.adaptar.MessageRoomListAdaptar;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.MessageRoomModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomsInGroupActivity extends BaseActivity implements View.OnClickListener, RequestListener, MessageRoomListAdaptar.MessageRoomListListener {

    public static final String TAG = "RoomsInGroupActivity";
    public static final String POST_ID = "post_id";
    public static final String GOODS_TITLE = "goods_title";
    public static final String GOODS_DETAIL = "goods_detail";

    private static final String REQUEST_GET_ROOMS = "get_rooms_info_ingroup";

    ImageButton img_back;
    TextView txt_name, txt_detail;
    RecyclerView recyclerview;

    private String requestType;
    private int mSelecteRoom = 0, mSelecteGroup = -1, mCurrentPos = 0;

    ArrayList<MessageRoomModel> roomModels = new ArrayList<>();
    MessageRoomListAdaptar roomListAdaptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digitalgoods_rooms_ingroup);

        setLightStatusBar();

        img_back = (ImageButton)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        txt_name = (TextView)findViewById(R.id.txt_name);
        txt_detail = (TextView)findViewById(R.id.txt_detail);

        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);
        recyclerview.setItemViewCacheSize(20);
        recyclerview.setDrawingCacheEnabled(true);
        recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        roomListAdaptar = new MessageRoomListAdaptar(this, roomModels);
        recyclerview.setAdapter(roomListAdaptar);
        roomListAdaptar.setClickListener(this);

        txt_name.setText(getIntent().getStringExtra(GOODS_TITLE));
//        txt_detail.setText(getIntent().getStringExtra(GOODS_DETAIL));
//        txt_detail.setText("(" + String.valueOf(roomModels.size()) + ")");

        mSelecteGroup = getIntent().getIntExtra(POST_ID, 0);

        getRooms(mSelecteGroup);

    }

    private void getRooms(int groupID) {

        mSelecteGroup = groupID;
        requestType = REQUEST_GET_ROOMS;

        if (Constants.isLifeToken(this)) {
            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getRoomsInGroup(this, this, token, groupID);
        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void leaveRoom(int roomid) {

        requestType = Constants.REQUEST_LEAVE_MESSAGE_ROOMS;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().deleteChatRoom(this, this, token, roomid, roomModels.get(mCurrentPos).isMute);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void muteRoom(int roomid) {

        requestType = Constants.REQUEST_MUTE_MESSAGE_ROOMS;

        if (Constants.isLifeToken(this)) {

            String token = Utility.getReadPref(this).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().muteChatRoom(this, this, token, roomid, true);

        } else {
            Constants.requestRefreshToken(this, this);
        }

    }

    private void parsingRoomsInfo(JSONObject jsonObject) {

        if (jsonObject == null) return;

        JSONObject group = jsonObject.optJSONObject("chat_room");
        if (group == null) return;

        JSONArray chatrooms = group.optJSONArray("items");
        if (chatrooms != null && chatrooms.length() > 0) {

            for (int i = 0; i<chatrooms.length(); i++) {

                MessageRoomModel roomModel = new MessageRoomModel();

                try {
                    JSONObject room = (JSONObject)chatrooms.get(i);

                    if (room == null) continue;

                    roomModel.roomID = room.optInt(Constants.ID);
                    roomModel.groupID = room.optInt("group_id");
                    roomModel.type = Constants.ROOM_NORMAL;
                    roomModel.isMute = room.optBoolean("muted");
                    roomModel.unreadMessageCNT = room.optInt("unread_message_count");
                    roomModel.lastMessageTime = room.optString("last_message_created_at");

                    JSONObject lastmsg = room.optJSONObject("last_message");
                    if (lastmsg != null) {

                        JSONObject msg = lastmsg.optJSONObject("message");
                        if (msg != null) roomModel.lastMessage = msg.optString("text");
                    }

                    JSONArray members = room.optJSONArray("members");
                    if (members != null && members.length() > 0) {

                        for (int k = 0; k < members.length(); k++) {

                            JSONObject userObj = (JSONObject)members.get(k);
                            if (userObj == null) continue;

                            JSONObject user = userObj.optJSONObject("user");
                            if (user == null) continue;

                            if (Utility.getReadPref(this).getStringValue(Constants.USERNAME).equals(user.optString(Constants.USERNAME))) continue;

                            roomModel.name = user.optString(Constants.USERNAME);
                            roomModel.username = user.optString(Constants.USERNAME);

                            JSONObject userProfileObj = user.optJSONObject("usersProfile");

                            if (userProfileObj != null) {

                                roomModel.firstName = userProfileObj.optString(Constants.FIRST_NAME);
                                roomModel.lastName = userProfileObj.optString(Constants.LAST_NAME);

                                JSONObject avatarObj = userProfileObj.optJSONObject(Constants.AVATAR);

                                if (avatarObj != null) {
                                    roomModel.avatar = avatarObj.optString("url");
                                } else {
                                    roomModel.avatar = "null";
                                }

                            } else {
                                roomModel.firstName = "";
                                roomModel.lastName = "";
                                roomModel.avatar = "null";
                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                roomModel.isSeller = false;

                roomModels.add(roomModel);

            }

            if (roomListAdaptar != null) roomListAdaptar.notifyDataSetChanged();

            txt_detail.setText("(" + String.valueOf(roomModels.size()) + ")");

        }

    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            finish();
            overridePendingTransition(R.anim.trans_finish_out, R.anim.trans_finish_in);
        }

    }

    @Override
    public void onClickRoom(int position) {

        String name = "";
        if (roomModels.get(position).firstName.equals("null") || roomModels.get(position).firstName.equals("") || roomModels.get(position).lastName.equals("null") || roomModels.get(position).lastName.equals("")) {
            name = roomModels.get(position).username;
        } else {
//            name = roomModels.get(position).lastName + " " + roomModels.get(position).firstName;
            name = roomModels.get(position).username;
        }

        mSelecteRoom = position;

        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra(Constants.TARGET, TAG);
        intent.putExtra(Constants.TYPE, Constants.ROOM_DIGITAL_GOODS);
        intent.putExtra(Constants.ID, roomModels.get(position).roomID);
        intent.putExtra(Constants.USERNAME, name);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    public void onClickMute(int position) {

        mCurrentPos = position;
        mSelecteRoom = roomModels.get(position).roomID;

        muteRoom(mSelecteRoom);

    }

    @Override
    public void onClickLeave(int position) {
        mCurrentPos = position;
        mSelecteRoom = roomModels.get(position).roomID;

        String message = getString(R.string.leave_chatroom_msg);

        new AlertView(message,null,  null, new String[]{getString(R.string.cancel), getString(R.string.ok)}, null,
                this, AlertView.Style.Alert, new OnItemClickListener(){
            public void onItemClick(Object o,int position){

                if (position == 1) {
                    leaveRoom(mSelecteRoom);
                }
            }

        }).show();
    }

    @Override
    public void onClickGroupInfo(int position) {

    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(this)) {

            switch (requestType){

                case REQUEST_GET_ROOMS:
                    parsingRoomsInfo(objResult);
                    break;
                case Constants.REQUEST_MUTE_MESSAGE_ROOMS:
                    MessageRoomModel messageRoomModel = roomModels.get(mCurrentPos);
                    messageRoomModel.isMute = objResult.optBoolean("is_mute");
                    messageRoomModel.lastMessageTime = objResult.optString("created_at");
                    roomModels.set(mCurrentPos, messageRoomModel);
                    roomListAdaptar.notifyDataSetChanged();
                    break;
                case Constants.REQUEST_LEAVE_MESSAGE_ROOMS:
                    roomModels.remove(mCurrentPos);
                    roomListAdaptar.notifyDataSetChanged();
                    break;
            }

        } else {

            Constants.saveRefreshToken(this, objResult);

            switch (requestType){
                case REQUEST_GET_ROOMS:
                    getRooms(mSelecteGroup);
                    break;
                case Constants.REQUEST_MUTE_MESSAGE_ROOMS:
                    muteRoom(mSelecteRoom);
                    break;
                case Constants.REQUEST_LEAVE_MESSAGE_ROOMS:
                    leaveRoom(mSelecteRoom);
                    break;
            }

        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        if (Utility.g_isCalledRefreshToken) {

            Utility.g_isCalledRefreshToken = false;

            Utility.showAlert(this, getString(R.string.oh_snap), getString(R.string.network_error_refreshtoken), getString(R.string.okay));

        }

        if (Constants.isLifeToken(this)) Utility.parseError(this, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
