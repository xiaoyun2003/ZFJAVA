import java.lang.reflect.Field;

public class Utils {
    //打印一个对象内部所有属性，该对象必须有实体类
    public static void LogObject(Object o){
        Field[] field=o.getClass().getDeclaredFields();
        for(int i=0;i<field.length ;i++){
            try {
                System.out.println("[LogInfo]:name:"+field[i].getName()+" value:"+field[i].get(o).toString());
            } catch (Exception e) {
                System.out.println("[LogInfo]:error:"+field[i]+" "+e.getLocalizedMessage());
            }
        }
    }
    public static void LogObjectArrary(Object[] os) {
        try {
            for (int i = 0; i < os.length; i++) {
                System.out.println("--------------------------");
                LogObject(os[i]);
                System.out.println("--------------------------");
            }
        }catch (Exception e){
            System.out.println("[LogInfo]error:"+e.getLocalizedMessage());
        }
    }
}
