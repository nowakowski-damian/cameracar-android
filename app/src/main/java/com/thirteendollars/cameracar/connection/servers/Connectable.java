package com.thirteendollars.cameracar.connection.servers;

import java.io.IOException;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 12/3/16
 * ============================================================================
 */

interface Connectable {
    void start();
    void stop();
    void send(byte[] data) throws IOException;
}
