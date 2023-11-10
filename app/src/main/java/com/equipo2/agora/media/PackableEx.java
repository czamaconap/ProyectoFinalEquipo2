package com.equipo2.agora.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
