package cn.org.upbnc.util.xml;

public class GetMessageId {
    private static int message_id = 100;

    public static int getId() {
        if (message_id > 65535)
            message_id = 100;
        else
            message_id++;
        return message_id;
    }
}
