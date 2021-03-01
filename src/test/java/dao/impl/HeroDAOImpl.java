package dao.impl;

import bean.Hero;
import dao.HeroDAO;
import service.HeroService;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 22:43
 */

public class HeroDAOImpl implements HeroDAO {
    HeroService heroService;

    @Override
    public Hero getHero(Integer id) {
        Hero hero = new Hero();
        hero.setId(id);
        hero.setName(this.toString());
        return hero;
    }
}
