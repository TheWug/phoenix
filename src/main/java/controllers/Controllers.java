package controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;

import hello.CustomAuthenticationDetails;
import hello.Auth;
import models.TwoFAUser;
import models.TwoFAUserDetailsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.zxing.WriterException;

@Controller
public class Controllers {
	Logger logger = LoggerFactory.getLogger(Controllers.class.getName());
	Auth auth;

	public Controllers() throws GeneralSecurityException {
		auth = new Auth();
	}

	@Autowired
	public TwoFAUserDetailsService userDetailsService;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@RequestMapping(value = "/enroll2fa", method = RequestMethod.POST)
	public String enroll(@RequestParam(value = "code1", required = false, defaultValue = "x") String code1,
			@RequestParam(value = "code2", required = false, defaultValue = "x") String code2, Model model) throws WriterException, IOException {

		CustomAuthenticationDetails details = (CustomAuthenticationDetails) SecurityContextHolder.getContext()
				.getAuthentication().getDetails();
		TwoFAUser user = (TwoFAUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String sharedSecret = user.getSharedSecret();
		long timeIndex = auth.getTimeIndex(System.currentTimeMillis());

		try {
			if (code1.equals("x") || code2.equals("x")) {
				model.addAttribute("errorMessage", "Enter codes!");
			} else if (auth.validateSecretCode(Integer.parseInt(code1), sharedSecret, timeIndex - 1)
					&& auth.validateSecretCode(Integer.parseInt(code2), sharedSecret, timeIndex)) {
				user.setTwoFactorEnabled(true);
				userDetailsService.save(user);
				model.addAttribute("enrollMessage", "You have successfully enabled 2 factor authentication.");
				return gotocp(model);
			} else {
				model.addAttribute("errorMessage", "Entered codes are wrong. Please re-scan your key and re-enter your passcodes.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "An error occurred (" + e.getClass().getName() + "). Please re-scan the key and re-enter your passcodes."); // will probably only ever be NumberFormatException
		}
		return enrollForm(model);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@RequestMapping(value = "/enroll2fa", method = RequestMethod.GET)
	public String enrollForm(Model model) throws WriterException, IOException {
		CustomAuthenticationDetails details = (CustomAuthenticationDetails) SecurityContextHolder.getContext()
				.getAuthentication().getDetails();
		TwoFAUser user = (TwoFAUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (validateEnrolled()) {
			user.setTwoFactorEnabled(false);
			userDetailsService.save(user);
			model.addAttribute("enrollMessage", "You have successfully disabled 2 factor authentication.");
			return gotocp(model);
		} else {

			String sharedSecret = auth.generateSharedSecret();
			// saving it to the database
			user.setSharedSecret(sharedSecret);
			userDetailsService.save(user);
			model.addAttribute("sharedSecret", sharedSecret);
			String googleChartURLFormat = "otpauth://totp/" + user.getUsername() + "@vmware.com?secret="
					+ URLEncoder.encode(sharedSecret, "UTF-8");
			System.out.println("url " + googleChartURLFormat);
			model.addAttribute("sharedSecretBase64", auth.generateQRCode(googleChartURLFormat));
			return "enrollform";
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@RequestMapping(value = "/verify2fa", method = RequestMethod.POST)
	public String verify2fa(@RequestParam(value = "authcode", required = true, defaultValue="x") String code, Model model) {

		logger.info("Validating authcode {} for user {}", code, SecurityContextHolder.getContext().getAuthentication()
				.getName());
		if(code.equals("x")) {
			model.addAttribute("errorMessage", "You have to enter Authentication code");
			return "2fa";
		}		
		else if (this.valdate2fa(code)) {
			return "home";
		} else {
			// SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
			model.addAttribute("errorMessage", "Entered Authentication code is wrong. Try again");
			return "2fa";
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@RequestMapping(value = "/controlPanel", method = RequestMethod.GET)
	public String gotocp(Model model) {
		if (validateEnrolled()) {
			model.addAttribute("isEnrolled", "Unenroll Two Factor Authentication");
		} else {
			model.addAttribute("isEnrolled", "Enroll Two Factor Authentication");
		}
		return "controlPanel";
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@RequestMapping(value = "/gohome", method = RequestMethod.GET)
	public String homefromCp() {
		return "home";
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean valdate2fa(String userCode) {

		CustomAuthenticationDetails details = (CustomAuthenticationDetails) SecurityContextHolder.getContext()
				.getAuthentication().getDetails();
		TwoFAUser user = (TwoFAUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String sharedSecret = user.getSharedSecret();

		try {
			long timeIndex = auth.getTimeIndex(System.currentTimeMillis());
			details.setTwoFAValid(auth.validateSecretCode(Long.parseLong(userCode), sharedSecret, timeIndex));
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}

		return details.isTwoFAValid();
	}

	private boolean validateEnrolled() {
		TwoFAUser user = (TwoFAUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user.isTwoFactorEnabled();
	}
}
