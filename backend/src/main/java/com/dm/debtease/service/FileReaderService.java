package com.dm.debtease.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface FileReaderService {
    ByteArrayInputStream readFileData(String filePath) throws IOException;
}
