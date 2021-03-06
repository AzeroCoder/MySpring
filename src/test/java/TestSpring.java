import annotation.Component;
import annotation.Inject;
import bean.Hero;
import main.Main;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import service.HeroService;

import java.io.IOException;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2020/9/13 18:20
 */
@Component
public class TestSpring {

    @Inject
    HeroService heroService;

    @BeforeClass
    public static void before() throws IOException, ClassNotFoundException {
    }

    @Test
    public void testIOC(){
        Boostrap.start(this);
//        heroService = Boostrap.getBean("heroService");
        Hero hero = heroService.getHero(4);
                System.out.println(hero);
        Hero hero1 = heroService.getHero(5);
                System.out.println(hero1);
        Assert.assertEquals(hero.getId(), 4);
    }

    @Test
    public void testAOP(){
        Hero hero = Boostrap.getBean("hero");
        System.out.println(hero);
        hero.hello();
        Assert.assertEquals(hero.getId(), 0);
    }
}
