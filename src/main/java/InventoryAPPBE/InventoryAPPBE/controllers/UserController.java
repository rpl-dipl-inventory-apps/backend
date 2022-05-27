package InventoryAPPBE.InventoryAPPBE.controllers;


import InventoryAPPBE.InventoryAPPBE.config.JwtTokenConfig;
import InventoryAPPBE.InventoryAPPBE.controllers.request.RequestLogin;
import InventoryAPPBE.InventoryAPPBE.controllers.response.ResponseLogin;
import InventoryAPPBE.InventoryAPPBE.helpers.BaseResponse;
import InventoryAPPBE.InventoryAPPBE.helpers.RequestHelper;
import InventoryAPPBE.InventoryAPPBE.models.Authentication ;
import InventoryAPPBE.InventoryAPPBE.models.User;
import InventoryAPPBE.InventoryAPPBE.repositories.authentication.AuthenticationRepository;
import InventoryAPPBE.InventoryAPPBE.repositories.user.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends AbstractController<User>{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenConfig jwtTokenConfig;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private RequestHelper requestHelper;

    @Value("${jwt.secret}")
    private String secretKeyToken;

    @Value("${jwt.refresh.secret}")
    private String refreshTokenSecret;

    @Value("${jwt.refresh.expiration}")
    private String refreshTokenExpiration;

    @Value("${jwt.expiration}")
    private String tokenExpiration;


    @PostMapping("")
    public ResponseEntity<BaseResponse<User>> create(@RequestBody User t, @RequestParam(required = false) Boolean supplier) {
        // membuat object response
        BaseResponse<User> response = new BaseResponse<>(null, null, null);
        try {
            // menginisialisasi object model user
            User userFoundByEmail = null;

            // mencari user berdasarkan email
            try {
                userFoundByEmail = userRepository.findByEmail(t.getEmail());
            }catch (NoResultException ignored){}

            // jika object user tidak sama dengan null
            if(userFoundByEmail != null) {
                // lempar error email sudah digunakan
                throw new DuplicateKeyException("email already exists");
            }

            // menginisialisasi object password encoder
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // mengubah password menjadi hash
            String hashedPassword = passwordEncoder.encode(t.getPassword());
            t.setPassword(hashedPassword);
            // memberikan nilai default role
            if(supplier){
                t.setRole("supplier");
            }else{
                t.setRole("default");
            }


            // menyimpan data user ke database
            User savedUser = userRepository.create(t);

            // menghapus password dari object user karena tidak perlu ditampilkan
            savedUser.setPassword(null);

            // mengisi object response dengan data user dan status success
            response.setData(savedUser);
            response.setMessage("success");
            response.setStatus("success");

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (DuplicateKeyException e){
            // mengisi object response dengan pesan error dan status error
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<BaseResponse<User>> findUserById() {
        // membuat object response
        BaseResponse<User> response = new BaseResponse<>(null, null, null);
        try {
            // mengambil data user dari user yang sedang login
            User loggedUser = requestHelper.getUserFromContext();

            // menghapus password dari object user karena tidak perlu ditampilkan
            loggedUser.setPassword(null);

            // mengisi object response dengan data user dan status success
            response.setMessage("success");
            response.setStatus("success");
            response.setData(loggedUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NumberFormatException e) {
            // mengisi object response dengan pesan error dan status error
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            // mengisi object response dengan pesan error dan status error
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<ResponseLogin>> login(@RequestBody RequestLogin req, HttpServletResponse responseHttp){
        // membuat object response
        BaseResponse<ResponseLogin> response = new BaseResponse<>(null, null, null);
        try {
            // mencari user berdasarkan email
            User userFoundByEmail = userRepository.findByEmail(req.getEmail());

            // menginisialisasi object response
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // membandingkan password dengan password yang ada di database
            boolean result = passwordEncoder.matches(req.getPassword(), userFoundByEmail.getPassword());

            // jika password tidak sesuai
            if(!result){
                // lempar error password salah
                throw new BadCredentialsException("password not match");
            }

            // mengiisi nilai umur jwt token untuk access token
            jwtTokenConfig.setJwtTokenValidity(Integer.parseInt(tokenExpiration));
            // mengisi nilai scret key jwt token untuk access token
            jwtTokenConfig.setSecret(secretKeyToken);
            // generate jwt token untuk access token
            String token = jwtTokenConfig.generateToken(userFoundByEmail);

            // mengisi nilai umur jwt token untuk refresh token
            jwtTokenConfig.setJwtTokenValidity(Integer.parseInt(refreshTokenExpiration));
            // mengisi nilai scret key jwt token untuk refresh token
            jwtTokenConfig.setSecret(refreshTokenSecret);
            // generate jwt token untuk refresh token
            String refreshToken = jwtTokenConfig.generateToken(userFoundByEmail);

            // menginisialisasi object authenication
            Authentication authentication = new Authentication();

            // mengisi nilai user id
            authentication.setUserId(userFoundByEmail.getId());

            // mengisi nilai refresh token
            authentication.setRefreshToken(refreshToken);

            // menyimpan authenication ke dalam database
            authenticationRepository.create(authentication);

            // membuat cookie untuk refresh token
            newCookieRefreshToken(responseHttp, refreshToken);

            // menginisialisasi object response login
            ResponseLogin responseLogin = new ResponseLogin();
            responseLogin.setToken(token);
            response.setData(responseLogin);
            response.setMessage("success");
            response.setStatus("success");

            // mengembalikan response dengan status success
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadCredentialsException | NoResultException e){
            // mengisi object response dengan pesan error dan status error
            response.setData(null);
            response.setMessage("email or password not match");
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // mengisi object response dengan pesan error dan status error
            e.printStackTrace();
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<ResponseLogin>> refresh(@CookieValue("sess_id") String refreshToken){
        BaseResponse<ResponseLogin> response = new BaseResponse<>(null, null, null);
        try {
            jwtTokenConfig.setSecret(refreshTokenSecret);
            jwtTokenConfig.getUserIdFromToken(refreshToken);
            Authentication auth = authenticationRepository.getByRefreshToken(refreshToken);
            List<User> user = userRepository.findById(auth.getUserId());
            if (user.isEmpty()) {
                throw new EntityNotFoundException("user not found");
            }
            jwtTokenConfig.setJwtTokenValidity(5);
            jwtTokenConfig.setSecret(secretKeyToken);
            String token = jwtTokenConfig.generateToken(user.get(0));

            ResponseLogin responseLogin = new ResponseLogin();

            responseLogin.setToken(token);
            response.setData(responseLogin);
            response.setMessage("success");
            response.setStatus("success");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ExpiredJwtException e){
            response.setData(null);
            response.setMessage("token expired");
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException | NoResultException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<ResponseLogin>> logout(@CookieValue("sess_id") String refreshToken, HttpServletResponse responseHttp){
        // menginisiasi object response
        BaseResponse<ResponseLogin> response = new BaseResponse<>(null, null, null);
        try {
            // menghapus refresh token dari cookie
            removeCookieRefreshToken(responseHttp);
            response.setData(null);
            response.setMessage("success");
            response.setStatus("success");

            // mengembalikan response dengan status success
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (ExpiredJwtException e){
            // mengisi object response dengan pesan error dan status error
            removeCookieRefreshToken(responseHttp);
            response.setData(null);
            response.setMessage("success");
            response.setStatus("success");

            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (EntityNotFoundException | NoResultException e){
            // mengisi object response dengan pesan error dan status error
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // mengisi object response dengan pesan error dan status error
            e.printStackTrace();
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/edit")
    public ResponseEntity<BaseResponse<User>> update(@RequestBody User t) {
        BaseResponse<User> response = new BaseResponse<>(null, null, null);
        try {
            User loggedUser = requestHelper.getUserFromContext();
            
            t.setId(loggedUser.getId());
            t.setPassword(loggedUser.getPassword());
            User savedUser = userRepository.update(t);
            savedUser.setPassword(null);
            response.setData(savedUser);
            response.setMessage("success");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ExpiredJwtException e){
            response.setData(null);
            response.setMessage("token expired");
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (NumberFormatException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<BaseResponse<List<User>>> getAll() {
        BaseResponse<List<User>> response = new BaseResponse<>(null, null, null);
        try {
            User loggedUser = requestHelper.getUserFromContext();
            if(!loggedUser.getRole().equals("admin")){
                response.setData(null);
                response.setMessage("access denied");
                response.setStatus("error");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            List<User> users = userRepository.findAll();
            response.setData(users);
            response.setMessage("success");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void newCookieRefreshToken(HttpServletResponse responseHttp, String refreshToken){
        responseHttp.setHeader(HttpHeaders.SET_COOKIE, String.format("sess_id=%s; SameSite=None; Max-Age=604800; path=/; Secure; HttpOnly", refreshToken));
    }

    private void removeCookieRefreshToken(HttpServletResponse responseHttp){
        responseHttp.setHeader(HttpHeaders.SET_COOKIE, String.format("sess_id=%s; SameSite=None; Max-Age=-1; path=/; Secure; HttpOnly", ""));
    }
}
