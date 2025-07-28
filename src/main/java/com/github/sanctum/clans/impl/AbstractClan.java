package com.github.sanctum.clans.impl;

import com.github.sanctum.clans.model.Clan;
import com.github.sanctum.panther.annotation.Note;

@Note("Use this to implement your own clan object. It will play into all of the rest of the code. Easy interjection")
public abstract class AbstractClan implements Clan {

    @Override
    public final Implementation getImplementation() {
        return Implementation.CUSTOM;
    }
}
