package bgu.spl.mics;

import java.io.IOException;

/**
 * a callback is a function designed to be called when a message is received.
 */
public interface Callback<T> {

    public void call(T c) throws IOException;

}
