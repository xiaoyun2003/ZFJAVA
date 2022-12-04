import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
ZF zf=new ZF();
//登录
User u=zf.Login("22080006","DQ200303.");
//前置数据，使用自主选课接口时，你必须调用此方法，这样才可以获得操作参数，否则功能无效
zf.getHomeDataXK(u);
//课程类型板块，专业选修课，大学体育（1），通识选修课
CourseXK[] cs=zf.getCoursesXKByHomeData(null);
        Utils.LogObjectArrary(cs);
//每个板块下面对应的课程，营销心理学，社会心理学，大数据导论.......
ClassXK[] cls=zf.getClassesXK(u,cs[0]);
        Utils.LogObjectArrary(cls);
//每个课程下面对应的教学班,张三教学班(j1-110),李四教学班(j2-113)...
      JXBXK[] jxbs= zf.getJXBsXK(u,cls[0]);
        Utils.LogObjectArrary(jxbs);
//选择教学班
String res=zf.selectClass(u,jxbs[0]);
System.out.println(res);



//获取我的课程表
 MyClass[] mcls=zf.getMyClasses(u,"2022","3");
 Utils.LogObjectArrary(mcls);

    }
}

