// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.llib.exceptions;

public class LeafiaDevFlaw extends RuntimeException {
    public LeafiaDevFlaw(String s) {
        super("\uD83C\uDF3F"+s);//super("\uE05E\u8AFA"+s);
    }
    public LeafiaDevFlaw(String s,Throwable e) {
        this(s+": "+e.getMessage());
        appendStackTrace(e);
    }
    public LeafiaDevFlaw(Throwable e) {
        this(e.toString());
        appendStackTrace(e);
    }
    public LeafiaDevFlaw appendStackTrace(Throwable e) {
        StackTraceElement[] stack = new StackTraceElement[e.getStackTrace().length+this.getStackTrace().length];
        System.arraycopy(this.getStackTrace(),0,stack,0,this.getStackTrace().length);
        System.arraycopy(e.getStackTrace(),0,stack,this.getStackTrace().length,e.getStackTrace().length);
        this.setStackTrace(stack);
        return this;
    }
}