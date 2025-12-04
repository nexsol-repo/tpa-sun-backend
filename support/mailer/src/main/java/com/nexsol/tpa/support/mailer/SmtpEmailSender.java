package com.nexsol.tpa.support.mailer;

import com.nexsol.tpa.core.domain.EmailSender;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

	private final JavaMailSender javaMailSender;

	@Override
	public void send(String toEmail, String authCode) {
		try {

			MimeMessage message = javaMailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(toEmail);
			helper.setSubject("[TPA KOREA] 태양광발전소종합보험 인증번호 안내");

			String htmlContent = getHtmlTemplate(authCode);
			helper.setText(htmlContent, true);

			javaMailSender.send(message);
			log.info("인증 메일 발송 성공: {}", toEmail);
		}
		catch (Exception e) {
			log.error("메일 발송 실패: {}", toEmail, e);
			throw new RuntimeException("메일 발송에 실패했습니다.", e);
		}
	}

	private String getHtmlTemplate(String authCode) {
		// role="presentation": 테이블을 레이아웃 용도로만 사용함을 명시 (시맨틱 웹 표준)
		// String.formatted() 사용 시 % 문자는 포맷 지정자로 인식되므로, 단순 문자 %를 표현하려면 %%로 이스케이프해야 합니다.
		// width='100%' -> width='100%%' 로 수정됨
		return """
				<!DOCTYPE html>
				<html lang="ko">
				<head>
				<title>TPA KOREA 인증번호</title>
				<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
				<meta name='viewport' content='width=device-width, initial-scale=1.0' />
				</head>
				<body style="margin: 0; padding: 0;">
				<table role="presentation" cellspacing='0' cellpadding='0' border='0' width='100%%' align='center'
				  style='border-collapse: separate; border-style: hidden; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border:0;'>
				  <tbody>
				    <tr>
				      <td align='center'
				        style='padding-top:30px; padding-bottom: 30px; padding-left: auto; padding-right: auto; background-color: #eee;'>
				        <table role="presentation" cellspacing='0' cellpadding='0' border='0' width='600' align='center'
				          style=' background-color: #fff; border-collapse: separate; border-style: hidden; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border:0; width: 600px;'>
				          <tbody>
				            <tr>
				              <td style='border: 0;'>
				                <table role="presentation" cellspacing='0' cellpadding='0' border='0' width='100%%' align='center'
				                  style='border-collapse: separate; border-style: hidden; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border:none; padding: 36px;'>
				                  <tbody>
				                    <tr>
				                      <td style='border: 0; padding: 8px 0; border-bottom: 1px solid #000;'>
				                        <h1
				                          style='font-size: 32px; font-weight:bold; line-height: 40px;margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: bold;'>
				                          인증번호 발급
				                        </h1>
				                      </td>
				                      <td
				                        style='border: 0; width: auto; padding: 8px 0; border-bottom: 1px solid #000;     text-align: right;'>
				                        태양광발전소종합보험
				                      </td>
				                    </tr>
				                    <tr>
				                      <td colspan='2' style='border: 0; padding: 16px 0; border-bottom: 1px solid #e7e9eb;'>
				                        <p
				                          style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                          안녕하세요,<br />
				                          고객님. 인증번호를 아래와 같이 보내드립니다.<br />
				                        </p>
				                        <br/>
				                        <p style=' color: #00B855;font-size: 16px;line-height: 24px;'>인증번호 :
				                          <b style='font-size: 24px; font-weight: bold;'>%s</b>
				                        </p>
				                        <br/>
				                        <p
				                          style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                          회원을 등록하지 않은 경우, 이 이메일을 무시하거나
				                          TPA KOREA 태양광발전소종합보험 고객센터(1644-9664)로 문의해 주시기 바랍니다.<br />
				                          좋은 하루 되시길 바랍니다.<br />
				                          감사합니다.
				                        </p>
				                      </td>
				                    </tr>
				                    <tr>
				                      <td colspan='2' style='border: 0; padding: 30px 0; border-bottom: 1px solid #000;'>
				                        <table role="presentation" cellspacing='0' cellpadding='0' border='0' width='100%%' align='center'
				                          style='border-collapse: separate; border-style: hidden; mso-table-lspace: 0pt; mso-table-rspace: 0pt;'>
				                          <tbody>
				                            <tr>
				                              <td colspan='3' style='border: 0; padding-bottom: 16px;'>
				                                <h2
				                                  style='font-size: 20px; line-height: 28px;margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: bold;'>
				                                  TPA KOREA 태양광발전소종합보험 고객센터
				                                </h2>
				                              </td>
				                            </tr>
				                            <tr>
				                              <td style='border: 0; width: 87px; padding: 4px 0;'>
				                                <p
				                                  style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                                  대표전화</p>
				                              </td>
				                              <td style='border: 0; width: 5px; padding: 4px;'>
				                                <p
				                                  style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                                  :</p>
				                              </td>
				                              <td style='border: 0; padding: 4px 0;'>
				                                <p
				                                  style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                                  1644-9664</p>
				                              </td>
				                            </tr>
				                            <tr>
				                              <td style='border: 0; width: 87px; padding: 4px 0;'>
				                                <p
				                                  style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                                  상담 시간</p>
				                              </td>
				                              <td style='border: 0; width: 5px; padding: 4px;'>
				                                <p
				                                  style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                                  :</p>
				                              </td>
				                              <td style='border: 0; padding: 4px 0;'>
				                                <p
				                                  style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                                  평일 09:00~18:00 (점심 12:00~13:00)</p>
				                              </td>
				                            </tr>
				                            <tr>
				                                                                      <td style='border: 0; width: 87px; padding: 4px 0;'>
				                                                                        <p
				                                                                          style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                                                                          웹사이트</p>
				                                                                      </td>
				                                                                      <td style='border: 0; width: 5px; padding: 4px;'>
				                                                                        <p
				                                                                          style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                                                                          :</p>
				                                                                      </td>
				                                                                      <td style='border: 0; padding: 4px 0;'>
				                                                                        <a href='' target='_blank'
				                                                                          style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>확정된
				                                                                          사이트 주소가 들어갑니다.</a>
				                                                                      </td>
				                                                                    </tr>
				                                                                    <tr>
				                                                                      <td style='border: 0; width: 87px; padding: 4px 0;'>
				                                                                        <p
				                                                                          style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                                                                          카톡 상담</p>
				                                                                      </td>
				                                                                      <td style='border: 0; width: 5px; padding: 4px;'>
				                                                                        <p
				                                                                          style='margin: 0;padding: 0;letter-spacing: -0.5px;font-weight: normal;font-size: 16px;line-height: 24px;'>
				                                                                          :</p>
				                                                                      </td>
				                                                                      <td style='border: 0;'>
				                                                                        <a href='' target='_blank' style='display: inline-block; width: 151px; height: 36px;'>
				                                                                          <img src='https://admin.wellparkgolf.com/img/btn_kakaochat.png' alt='카카오톡 상담하기'
				                                                                            style='display: block;' />
				                                                                        </a>
				                                                                      </td>
				                                                                    </tr>
				                          </tbody>
				                        </table>
				                      </td>
				                    </tr>
				                  </tbody>
				                </table>
				              </td>
				            </tr>
				          </tbody>
				        </table>
				      </td>
				    </tr>
				  </tbody>
				</table>
				</body>
				</html>
				"""
			.formatted(authCode);
	}

}
