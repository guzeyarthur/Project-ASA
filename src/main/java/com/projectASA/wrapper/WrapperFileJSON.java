package com.projectASA.wrapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class WrapperFileJSON {



    public BufferedWriter getBufferWriterTrue(String PATH) throws IOException {

        return new BufferedWriter(new FileWriter(PATH, true));

    }

}
