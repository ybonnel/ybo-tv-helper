package fr.ybo.modele;

import com.google.common.base.Function;
import fr.ybo.xmltv.Channel;

import java.io.Serializable;

public class TransformChannels implements Function<Channel, ChannelForNoSql>, Serializable {

    @Override
    public ChannelForNoSql apply(Channel channel) {
        return ChannelForNoSql.fromChannel(channel);
    }
}
