package service;

import annotation.Component;
import annotation.Inject;
import bean.Hero;
import dao.HeroDAO;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 22:51
 */
@Component
public class HeroService {
    @Inject
    HeroDAO heroDAO;

    public Hero getHero(Integer id){
        return heroDAO.getHero(id);
    }
}
