package main;

import annotation.Inject;
import bean.Hero;
import org.junit.Assert;
import service.HeroService;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/3/1 19:26
 */

public class Main {

    @Inject
    HeroService heroService;
    public void main(){
        Hero hero = heroService.getHero(4);
        System.out.println(hero);
        Hero hero1 = heroService.getHero(5);
        System.out.println(hero1);
        Assert.assertEquals(hero.getId(), 4);
    }
}
