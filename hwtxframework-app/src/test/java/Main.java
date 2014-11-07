import javax.el.ELProcessor;

/**
 * Created by panye on 2014/10/31.
 */
public class Main {
    public static void main(String[] args) {
        ELProcessor elp = new ELProcessor();
        elp.setValue("a", "2%");
        System.out.println(elp.eval("a < '3.3%'"));
    }
}
