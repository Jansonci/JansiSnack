package tech.wetech.service.service;
// 会话管理
import org.springframework.stereotype.Service;
import tech.wetech.api.dto.UserinfoDTO;
//import tech.wetech.service.model.User;
//import tech.wetech.service.model.UserCredential;
import tech.wetech.api.model.User;
import tech.wetech.api.model.UserCredential;


/**
 * @author Jansonci
 */
@Service
public interface SessionService {

  UserinfoDTO login(String username, String password);

  void logout(String token);

  boolean isLogin(String token);

  UserinfoDTO getLoginUserInfo(String token);

  void refresh();

  void creatCredential(String credential, String identifier, UserCredential.IdentityType identityType, User user);

}
