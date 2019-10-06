package util.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * This <code>Writer</code> writes everything it receives to the list 
 * of <code>Writer</code>s it decorates. No attempts are made to recover
 * from errors: if one of the decorated writers throws an 
 * <code>IOEcxeption</code>, this writer will also fail (not writing
 * to the following <code>Writer</code>s in the list!). Closing
 * one of the streams e.g. from outside this decorater will cause
 * this kind of exception!
 * 
 * @author Peter Van Weert
 */
public class WritersWriter extends Writer {
    /**
     * The decorated writers.
     */
    private Writer[] decorated;
    
    /**
     * Creates a new <code>WritersWriter</code> decorating the given
     * writers.
     * 
     * @param decorated
     *  The list of <code>Writer</code>s this <code>WritersWriter</code>
     *  decorates.
     *  
     * @throws NullPointerException
     *  If the argument given is a null-pointer.
     */
    public <W extends Writer> WritersWriter(List<W> decorated) throws NullPointerException {
        this.decorated = 
            decorated.toArray(new Writer[decorated.size()]); 
    }
    
    /**
     * Creates a new <code>WritersWriter</code> decorating the given
     * writers.
     * 
     * @param decorated
     *  The <code>Writer</code>s this <code>WritersWriter</code>
     *  decorates.
     * 
     * @throws NullPointerException
     *  If the argument given is a null-pointer.
     */
    public WritersWriter(Writer... decorated) {
        if (decorated == null) throw new NullPointerException();
        this.decorated = decorated;
    }
    
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (Writer writer : decorated) writer.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        for (Writer writer : decorated) writer.flush();
    }

    @Override
    public void close() throws IOException {
        for (Writer writer : decorated) writer.close();
    }
    
    @Override
    public Writer append(char c) throws IOException {
        for (Writer writer : decorated) writer.append(c);
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        for (Writer writer : decorated) writer.append(csq, start, end);
        return this;
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        for (Writer writer : decorated) writer.append(csq);
        return this;
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        for (Writer writer : decorated) writer.write(cbuf);
    }

    @Override
    public void write(int c) throws IOException {
        for (Writer writer : decorated) writer.write(c);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        for (Writer writer : decorated) writer.write(str, off, len);
    }

    @Override
    public void write(String str) throws IOException {
        for (Writer writer : decorated) writer.write(str);
    }
}
