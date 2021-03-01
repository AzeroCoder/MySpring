package dao.impl;

import bean.Hero;
import dao.HeroDAO;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 22:43
 */

public class HeroDAOImpl implements HeroDAO {
    @Override
    public Hero getHero(Integer id) {
        Hero hero = new Hero();
        hero.setId(id);
        hero.setName(this.toString());
        return hero;
    }
}
