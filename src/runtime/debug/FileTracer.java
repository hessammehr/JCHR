package runtime.debug;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileTracer extends OutputStreamTracer {

    public FileTracer(File file) throws FileNotFoundException {
        super(new FileOutputStream(file));
    }
    
    public FileTracer(File file, boolean append) throws FileNotFoundException {
        super(new FileOutputStream(file, append));
    }
    
    public FileTracer(FileDescriptor fdObj) throws FileNotFoundException {
        super(new FileOutputStream(fdObj));
    }
    
    public FileTracer(String name) throws FileNotFoundException {
        super(new FileOutputStream(name));
    }
    
    public FileTracer(String name, boolean append) throws FileNotFoundException {
        super(new FileOutputStream(name, append));
    }
}
