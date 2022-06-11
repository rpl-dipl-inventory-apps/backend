package InventoryAPPBE.InventoryAPPBE;

import InventoryAPPBE.InventoryAPPBE.config.JwtAuthenticationEntryPoint;
import InventoryAPPBE.InventoryAPPBE.config.JwtRequestFilter;
import InventoryAPPBE.InventoryAPPBE.config.JwtTokenConfig;
import InventoryAPPBE.InventoryAPPBE.controllers.UserController;
import InventoryAPPBE.InventoryAPPBE.controllers.request.RequestLogin;
import InventoryAPPBE.InventoryAPPBE.controllers.response.ResponseLogin;
import InventoryAPPBE.InventoryAPPBE.helpers.BaseResponse;
import InventoryAPPBE.InventoryAPPBE.helpers.RequestHelper;
import InventoryAPPBE.InventoryAPPBE.models.Authentication;
import InventoryAPPBE.InventoryAPPBE.models.User;
import InventoryAPPBE.InventoryAPPBE.repositories.authentication.AuthenticationRepository;
import InventoryAPPBE.InventoryAPPBE.repositories.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.NoResultException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenConfig jwtTokenConfig;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationRepository authenticationRepository;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private RequestHelper requestHelper;

    @Value("${jwt.secret}")
    private String secretKeyToken;

    @Value("${jwt.refresh.secret}")
    private String refreshTokenSecret;

    @Value("${jwt.refresh.expiration}")
    private String refreshTokenExpiration;

    @Value("${jwt.expiration}")
    private String tokenExpiration;

    @Test
    public void successfulLogin() throws Exception {
        String password = "password";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        RequestLogin requestLogin = new RequestLogin();
        requestLogin.setEmail("test@gmail.com");
        requestLogin.setPassword(password);

        User user = new User();
        user.setUsername("test");
        user.setPassword(encodedPassword);
        user.setEmail("test@gmail.com");
        user.setRole("default");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(requestLogin);

        ResponseLogin responseLogin = new ResponseLogin();
        BaseResponse<ResponseLogin> response = new BaseResponse<>(null, null, null);
        response.setData(responseLogin);
        response.setStatus("success");
        response.setMessage("success");
        String responseJson = ow.writeValueAsString(response);


        when(userRepository.findByEmail(requestLogin.getEmail())).thenReturn(user);

        mockMvc.perform(
                post("/users/login")
                        .contentType("application/json")
                        .content(requestJson)
        ).andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public  void shouldBadRequestNoEmail() throws Exception {
        String password = "password";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        RequestLogin requestLogin = new RequestLogin();
        requestLogin.setEmail("");
        requestLogin.setPassword(password);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(requestLogin);

        BaseResponse<ResponseLogin> response = new BaseResponse<>(null, null, null);
        response.setStatus("failed");
        response.setMessage("email or password not match");
        String responseJson = ow.writeValueAsString(response);

        when(userRepository.findByEmail(requestLogin.getEmail())).thenThrow(new NoResultException());

        mockMvc.perform(
                post("/users/login")
                        .contentType("application/json")
                        .content(requestJson)
        ).andExpect(status().isBadRequest())
                .andExpect(content().json(responseJson));
    }

    @Test
    public  void shouldBadRequestNoPassword() throws Exception {
        String password = "password";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        RequestLogin requestLogin = new RequestLogin();
        requestLogin.setEmail("test@gmail.com");
        requestLogin.setPassword("");

        User user = new User();
        user.setUsername("test");
        user.setPassword(encodedPassword);
        user.setEmail("test@gmail.com");
        user.setRole("default");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(requestLogin);

        BaseResponse<ResponseLogin> response = new BaseResponse<>(null, null, null);
        response.setStatus("failed");
        response.setMessage("email or password not match");
        String responseJson = ow.writeValueAsString(response);

        when(userRepository.findByEmail(requestLogin.getEmail())).thenReturn(user);

        mockMvc.perform(
                        post("/users/login")
                                .contentType("application/json")
                                .content(requestJson)
                ).andExpect(status().isBadRequest())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void shouldBadRequestEmailNotFound() throws Exception {
        String password = "password";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        RequestLogin requestLogin = new RequestLogin();
        requestLogin.setEmail("test@gmail.com");
        requestLogin.setPassword(password);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(requestLogin);

        BaseResponse<ResponseLogin> response = new BaseResponse<>(null, null, null);
        response.setStatus("failed");
        response.setMessage("email or password not match");
        String responseJson = ow.writeValueAsString(response);

        when(userRepository.findByEmail(requestLogin.getEmail())).thenThrow(new NoResultException());

        mockMvc.perform(
                post("/users/login")
                        .contentType("application/json")
                        .content(requestJson)
        ).andExpect(status().isBadRequest())
                .andExpect(content().json(responseJson));
    }
}
