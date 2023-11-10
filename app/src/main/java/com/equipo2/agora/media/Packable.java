package com.equipo2.agora.media;

/**
 * Created by Li on 10/1/2016.
 */
public interface Packable {
    com.equipo2.agora.media.ByteBuf marshal(ByteBuf out);
}
