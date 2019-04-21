package top.andnux.diffupdate;

public class Bspatch {
    static {
        try {
            System.loadLibrary("bspatch");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public native static int bspatch(String oldFile, String newFile, String patchFile);
}
