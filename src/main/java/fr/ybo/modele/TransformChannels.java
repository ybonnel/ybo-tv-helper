package fr.ybo.modele;

import com.google.common.base.Function;
import fr.ybo.xmltv.Channel;

import java.io.Serializable;

public class TransformChannels implements Function<Channel, ChannelForCouchBase>, Serializable {

    @Override
    public ChannelForCouchBase apply(Channel channel) {
        return ChannelForCouchBase.fromChannel(channel);
    }
}
