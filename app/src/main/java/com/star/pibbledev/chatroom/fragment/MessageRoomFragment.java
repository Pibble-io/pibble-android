package com.star.pibbledev.chatroom.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.star.pibbledev.dashboard.DashboardActivity;
import com.star.pibbledev.R;
import com.star.pibbledev.chatroom.activity.RoomsInGroupActivity;
import com.star.pibbledev.chatroom.adaptar.MessageRoomListAdaptar;
import com.star.pibbledev.chatroom.activity.ChatRoomActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.customview.EndlessRecyclerViewScrollListener;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.global.model.MessageRoomModel;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageRoomFragment extends Fragment implements View.OnClickListener, RequestListener, MessageRoomListAdaptar.MessageRoomListListener {

    public static final String TAG = "MessageRoomFragment";

    private static final String GET_DIGITAL_GOODS_ROOMS = "get_digital_goods_rooms";
    private static final String GET_PERSONAL_ROOMS = "get_personal_rooms";

    private Context mContext;

    private ImageButton img_back;
    private RecyclerView recyclerview;
    private NavigationTabStrip tab_bar;

    private String requestType;
    private int mCurrentPage = 0, mSelecteRoom = -1;

    private ArrayList<MessageRoomModel> roomModels = new ArrayList<>();
    private MessageRoomListAdaptar roomListAdaptar;

    private boolean isAddedScrollListener = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message_room, container, false);

        mContext = getActivity();

        img_back = (ImageButton)view.findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        recyclerview = (RecyclerView)view.findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);
        recyclerview.setItemViewCacheSize(20);
        recyclerview.setDrawingCacheEnabled(true);
        recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        roomListAdaptar = new MessageRoomListAdaptar(mContext, roomModels);
        recyclerview.setAdapter(roomListAdaptar);
        roomListAdaptar.setClickListener(this);

        tab_bar = (NavigationTabStrip) view.findViewById(R.id.tab_bar);

        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if (menuVisible) {

            roomModels.clear();

            getPersonalRooms(1);

            tab_bar.setTitles(getString(R.string.personal_msg), getString(R.string.goods_room));
            tab_bar.setTabIndex(0, true);

            tab_bar.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
                @Override
                public void onStartTabSelected(String title, int index) {

                }

                @Override
                public void onEndTabSelected(String title, int index) {

                    roomModels.clear();
                    roomListAdaptar.notifyDataSetChanged();

                    switch (index){
                        case 0:
                            getPersonalRooms(1);
                            break;

                        case 1:
                            getDigitalgoodsRooms(1);
                            break;
                    }

                }
            });

            if (!isAddedScrollListener) {

                isAddedScrollListener = true;

                recyclerview.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) recyclerview.getLayoutManager()) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {

                        switch (tab_bar.getTabIndex()) {
                            case 0:
                                getPersonalRooms(page + 1);
                                break;
                            case 1:
                                getDigitalgoodsRooms(page + 1);
                                break;
                        }

                    }

                    @Override
                    public void onScrolledUp() {

                    }

                    @Override
                    public void onScrolledDown() {

                    }
                });
            }

        } else {

            if (roomModels != null && roomModels.size() > 0) {
                if (roomListAdaptar != null) roomListAdaptar.clear();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSelecteRoom != -1) {

            if (roomModels.size() > mSelecteRoom) {

                MessageRoomModel roomModel = roomModels.get(mSelecteRoom);
                roomModel.unreadMessageCNT = 0;
                roomModels.set(mSelecteRoom, roomModel);
                if (roomListAdaptar != null) roomListAdaptar.notifyItemChanged(mSelecteRoom);

            }

        }
    }

    private void getDigitalgoodsRooms(int page) {

        requestType = GET_DIGITAL_GOODS_ROOMS;
        mCurrentPage = page;

        if (Constants.isLifeToken(mContext)) {

            String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);

            ServerRequest.getSharedServerRequest().getDigitalgoodsRooms(this, mContext, token, String.valueOf(page), "5");

        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }

    private void getPersonalRooms(int page) {

        requestType = GET_PERSONAL_ROOMS;
        mCurrentPage = page;

        if (Constants.isLifeToken(mContext)) {

            String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);

            ServerRequest.getSharedServerRequest().getPersonalRooms(this, mContext, token, String.valueOf(page), "5");

        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }

    private void leaveRoom(int roomid) {

        requestType = Constants.REQUEST_LEAVE_MESSAGE_ROOMS;

        if (Constants.isLifeToken(mContext)) {

            String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().deleteChatRoom(this, mContext, token, roomid, roomModels.get(mCurrentPage).isMute);

        } else {
            Constants.requestRefreshToken(mContext, this);
        }

    }

    private void muteRoom(int roomid) {

        requestType = Constants.REQUEST_MUTE_MESSAGE_ROOMS;

        if (Constants.isLifeToken(mContext)) {

            String token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().muteChatRoom(this, mContext, token, roomid, true);

        } else {
            Constants.requestRefreshToken(mContext, this);
        }
    }

    private void parsingPersonalRoomsData(JSONObject jsonObject) {

        if (jsonObject == null) return;

        int cnt_rooms = 0;
        if (roomModels != null && roomModels.size() > 0) cnt_rooms = roomModels.size();

        JSONArray itemAry = jsonObject.optJSONArray("items");
        if (itemAry != null && itemAry.length() > 0) {

            for (int i = 0; i < itemAry.length(); i++) {

                try {

                    JSONObject object = (JSONObject)itemAry.get(i);

                    if (object == null) continue;

                    MessageRoomModel roomModel = new MessageRoomModel();

//                    roomModel.groupID = object.optInt("group_id");
                    roomModel.roomID = object.optInt("id");

                    boolean isLoaded = false;

                    if (cnt_rooms > 0) {
                        for (int k = 0; k < cnt_rooms; k++) {

                            MessageRoomModel model = roomModels.get(k);
                            if (model.roomID == roomModel.roomID) {
                                isLoaded = true;
                            }
                        }
                    }

                    if (isLoaded) continue;

                    roomModel.type = object.optString("type");
//                    roomModel.postID = object.optInt("post_id");
                    roomModel.isMute = object.optBoolean("muted");
                    roomModel.unreadMessageCNT = object.optInt("unread_message_count");
//                    roomModel.lastMessageTime = object.optString("last_message_created_at");

                    JSONObject lastmsg = object.optJSONObject("last_message");
                    if (lastmsg != null) {

                        JSONObject msg = lastmsg.optJSONObject("message");
                        if (msg != null) {
                            roomModel.lastMessage = msg.optString("text");
                        }

                        roomModel.lastMessageTime = lastmsg.optString("created_at");
                    }

//                    if (roomModel.type.equals(Constants.ROOM_GOODS)) {
//
//                        JSONObject groupObj = object.optJSONObject("group");
//                        if (groupObj != null) {
//
//                            JSONObject post = groupObj.optJSONObject("post");
//                            if (post != null) {
//
//                                roomModel.isDeleted = post.optString("deleted_at") == null || post.optString("deleted_at").equals("null");
//
//                                JSONArray media = post.optJSONArray("media");
//                                if (media != null && media.length() > 0) {
//
//                                    JSONObject mediaObj = (JSONObject)media.get(0);
//                                    if (mediaObj != null) roomModel.avatar = mediaObj.optString("thumbnail");
//                                }
//
//                                JSONObject commerce = post.optJSONObject("commerce");
//                                if (commerce != null) {
//                                    roomModel.name = commerce.optString("name");
//                                    roomModel.goodsPrice = commerce.optInt("price");
//                                }
//
//                            } else {
//                                roomModel.isDeleted = false;
//                            }
//
//                        } else {
//                            continue;
//                        }
//
//                        JSONArray members = object.optJSONArray("all_members");
//                        if (members != null && members.length() > 0) {
//
//                            for (int k = 0; k < members.length(); k++) {
//
//                                JSONObject userObj = (JSONObject)members.get(k);
//                                if (userObj == null) continue;
//
//                                JSONObject user = userObj.optJSONObject("user");
//                                if (user == null) continue;
//
//                                if (Utility.getReadPref(mContext).getStringValue(Constants.USERNAME).equals(user.optString(Constants.USERNAME))) continue;
//
//                                roomModel.username = user.optString(Constants.USERNAME);
//
//                                JSONObject userProfileObj = user.optJSONObject("usersProfile");
//
//                                if (userProfileObj != null) {
//
//                                    roomModel.firstName = userProfileObj.optString(Constants.FIRST_NAME);
//                                    roomModel.lastName = userProfileObj.optString(Constants.LAST_NAME);
//
//                                } else {
//                                    roomModel.firstName = "";
//                                    roomModel.lastName = "";
//                                    roomModel.username = "";
//                                }
//
//                            }
//
//                        }
//
//                        roomModel.isSeller = false;
//
//                        roomModels.add(roomModel);
//
////                        roomModel.firstName = "";
////                        roomModel.lastName = "";
//
//                    } else

                    if (roomModel.type.equals(Constants.ROOM_NORMAL)) {

                        JSONArray members = object.optJSONArray("members");
                        if (members != null && members.length() > 0) {

                            for (int k = 0; k < members.length(); k++) {

                                JSONObject userObj = (JSONObject)members.get(k);
                                if (userObj == null) continue;

                                JSONObject user = userObj.optJSONObject("user");
                                if (user == null) continue;

                                if (Utility.getReadPref(mContext).getStringValue(Constants.USERNAME).equals(user.optString(Constants.USERNAME))) continue;

                                roomModel.name = user.optString(Constants.USERNAME);
                                roomModel.username = user.optString(Constants.USERNAME);

//                                JSONObject userProfileObj = user.optJSONObject("usersProfile");

//                                if (userProfileObj != null) {

                                roomModel.firstName = user.optString(Constants.FIRST_NAME);
                                roomModel.lastName = user.optString(Constants.LAST_NAME);

                                roomModel.avatar = user.optString(Constants.AVATAR);

//                                if (avatarObj != null) {
//                                    roomModel.avatar = avatarObj.optString("url");
//                                } else {
//                                    roomModel.avatar = "null";
//                                }

//                                } else {
//                                    roomModel.firstName = "";
//                                    roomModel.lastName = "";
//                                    roomModel.avatar = "null";
//                                    roomModel.username = "";
//                                }

                            }

                        }

                        roomModel.isSeller = false;

                        if (roomModel.username != null) roomModels.add(roomModel);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

        if (roomListAdaptar != null && tab_bar.getTabIndex() == 0) roomListAdaptar.notifyDataSetChanged();

    }

    private void parsingDigitalRoomsData(JSONObject jsonObject) {

        if (jsonObject == null) return;

        int cnt_rooms = 0;
        if (roomModels != null && roomModels.size() > 0) cnt_rooms = roomModels.size();

        JSONArray groupAry = jsonObject.optJSONArray("items");
        if (groupAry != null && groupAry.length() > 0) {
            for (int i = 0; i < groupAry.length(); i++) {

                try {

                    JSONObject groupObj = (JSONObject)groupAry.get(i);

                    if (groupObj == null) continue;

                    MessageRoomModel roomModel = new MessageRoomModel();

                    roomModel.groupID = groupObj.optInt(Constants.ID);

                    boolean isLoaded = false;

                    if (cnt_rooms > 0) {
                        for (int k = 0; k < cnt_rooms; k++) {

                            MessageRoomModel model = roomModels.get(k);
                            if (model.groupID == roomModel.groupID) {
                                isLoaded = true;
                            }
                        }
                    }

                    if (isLoaded) continue;
                    roomModel.postID = groupObj.optInt(Constants.POSTID);
                    roomModel.unreadMessageCNT = groupObj.optInt("unread_message_count");

                    JSONObject lastmsg = groupObj.optJSONObject("last_message");
                    if (lastmsg != null) {

                        JSONObject msg = lastmsg.optJSONObject("message");
                        if (msg != null) roomModel.lastMessage = msg.optString("text");
                        roomModel.lastMessageTime = lastmsg.optString("created_at");
                    }

                    JSONObject post = groupObj.optJSONObject("post");
                    if (post != null) {

                        roomModel.postID = post.optInt(Constants.ID);
                        roomModel.isDeleted = true;
                        roomModel.type = post.optString(Constants.TYPE);

                        JSONArray mediaAry = post.optJSONArray("media");
                        if (mediaAry != null && mediaAry.length() > 0) {

                            JSONObject media = (JSONObject)mediaAry.get(0);
                            roomModel.avatar = media.optString("thumbnail");

                        }

                        JSONObject userObj = post.optJSONObject("user");
                        if (userObj != null) roomModel.username = userObj.optString(Constants.USERNAME);

                        JSONObject commerce = post.optJSONObject(Constants.COMMERCE);
                        if (commerce != null) {
                            roomModel.name = commerce.optString(Constants.NAME);
                            roomModel.goodsPrice = commerce.optInt(Constants.PRICE);
                        }

                        JSONObject goods = post.optJSONObject(Constants.GOODS);
                        if (goods != null) {
                            roomModel.name = goods.optString(Constants.TITLE);
                            roomModel.goodsPrice = goods.optInt(Constants.PRICE);
                        }

                    } else {
                        continue;
                    }

                    JSONObject chat_room = groupObj.optJSONObject("chat_room");

                    if (chat_room != null) {

                        JSONArray chatItems = chat_room.optJSONArray("items");
                        if (chatItems != null && chatItems.length() > 0) {
                            roomModel.chatRoomCNT = chatItems.length();
                            roomModel.roomID = ((JSONObject)chatItems.get(0)).optInt(Constants.ID);
                        }
                    }

                    roomModel.isSeller = groupObj.optInt("owner") == Utility.getReadPref(mContext).getIntValue(Constants.ID);
//                        roomModel.isSeller = true;

                    roomModel.firstName = "";
                    roomModel.lastName = "";

                    roomModels.add(roomModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        if (roomListAdaptar != null && tab_bar.getTabIndex() == 1) roomListAdaptar.notifyDataSetChanged();

    }

    @Override
    public void onClick(View view) {

        if (view == img_back) {

            if ((DashboardActivity)getActivity() != null) ((DashboardActivity)getActivity()).back();
        }

    }

    @Override
    public void succeeded(JSONObject objResult) {

        if (Constants.isLifeToken(mContext)) {

            switch (requestType) {

                case GET_DIGITAL_GOODS_ROOMS:
                    parsingDigitalRoomsData(objResult);
                    break;

                case GET_PERSONAL_ROOMS:
                    parsingPersonalRoomsData(objResult);
                    break;

                case Constants.REQUEST_LEAVE_MESSAGE_ROOMS:

                    roomModels.remove(mCurrentPage);
                    roomListAdaptar.notifyDataSetChanged();

                    break;

                case Constants.REQUEST_MUTE_MESSAGE_ROOMS:

                    MessageRoomModel messageRoomModel = roomModels.get(mCurrentPage);
                    messageRoomModel.isMute = objResult.optBoolean("is_mute");
                    messageRoomModel.lastMessageTime = objResult.optString("created_at");
                    roomModels.set(mCurrentPage, messageRoomModel);
                    roomListAdaptar.notifyDataSetChanged();

                    break;
            }

        } else {

            Constants.saveRefreshToken(mContext, objResult);

            switch (requestType) {

                case GET_DIGITAL_GOODS_ROOMS:
                    getDigitalgoodsRooms(mCurrentPage);
                    break;

                case GET_PERSONAL_ROOMS:
                    getPersonalRooms(mCurrentPage);
                    break;

                case Constants.REQUEST_LEAVE_MESSAGE_ROOMS:
                    leaveRoom(mSelecteRoom);
                    break;

                case Constants.REQUEST_MUTE_MESSAGE_ROOMS:
                    muteRoom(mSelecteRoom);
                    break;
            }

        }

    }

    @Override
    public void failed(String strError, int errorCode) {

        if (Utility.g_isCalledRefreshToken) {

            Utility.g_isCalledRefreshToken = false;

            Utility.showAlert((AppCompatActivity)mContext, getString(R.string.oh_snap), getString(R.string.network_error_refreshtoken), getString(R.string.okay));

        }

        if (Constants.isLifeToken(mContext)) Utility.parseError((AppCompatActivity) mContext, strError);
    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

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

        Intent intent = new Intent(mContext, ChatRoomActivity.class);
        intent.putExtra(Constants.TARGET, TAG);
        intent.putExtra(Constants.TYPE, roomModels.get(position).type);
        intent.putExtra(Constants.ID, roomModels.get(position).roomID);
        intent.putExtra(Constants.USERNAME, name);
        mContext.startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

    }

    @Override
    public void onClickMute(int position) {

        mCurrentPage = position;
        mSelecteRoom = roomModels.get(position).roomID;

        muteRoom(mSelecteRoom);
    }

    @Override
    public void onClickLeave(int position) {

        mCurrentPage = position;
        mSelecteRoom = roomModels.get(position).roomID;

        String message = getString(R.string.leave_chatroom_msg);

        new AlertView(message,null,  null, new String[]{getString(R.string.cancel), getString(R.string.ok)}, null,
                mContext, AlertView.Style.Alert, new OnItemClickListener(){
            public void onItemClick(Object o,int position){

                if (position == 1) {
                    leaveRoom(mSelecteRoom);
                }
            }

        }).show();

    }

    @Override
    public void onClickGroupInfo(int position) {

        mSelecteRoom = -1;

        String title = roomModels.get(position).name;
        String detail = String.format("%s %d %s, %s %.2f %s", getString(R.string.price), roomModels.get(position).goodsPrice, getString(R.string.pib), getString(R.string.reward) ,roomModels.get(position).goodsPrice * 0.15, getString(R.string.pib));

        Intent intent = new Intent(mContext, RoomsInGroupActivity.class);
        intent.putExtra(RoomsInGroupActivity.POST_ID, roomModels.get(position).postID);
        intent.putExtra(RoomsInGroupActivity.GOODS_TITLE, title);
        intent.putExtra(RoomsInGroupActivity.GOODS_DETAIL, detail);
        mContext.startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

    }
}
