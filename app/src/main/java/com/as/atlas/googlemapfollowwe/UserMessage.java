package com.as.atlas.googlemapfollowwe;

/**
 * Created by atlas on 2016/8/24.
 */
public class UserMessage {

    private static final String TAG = UserMessage.class.getSimpleName();
    public String user;
    public String msg;
    public String timestamp;
    public UserMessage() {}
    public UserMessage(String user, String msg, String timestamp) {
        this.user = user;
        this.msg = msg;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof UserMessage) )  return false;
        UserMessage u = (UserMessage) o;

        return this.user.equals(u.user) &&
                this.msg.equals(u.msg) &&
                this.timestamp.equals(u.timestamp);
    }

    @Override
    public String toString() {
        return "[" + TAG + "]" +
                " user" + user +
                " msg" + msg +
                " timestamp" + timestamp;
    }
}
