package ramdan.file.line.token;


public interface Destroyable {
    void destroy() throws DestroyFailedException;
//    {
//        throw new DestroyFailedException();
//    }

    boolean isDestroyed();
}
