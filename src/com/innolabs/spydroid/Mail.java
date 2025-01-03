package com.innolabs.spydroid;

import java.util.Date; 
import java.util.Properties; 
import javax.activation.CommandMap; 
import javax.activation.DataHandler; 
import javax.activation.DataSource; 
import javax.activation.FileDataSource; 
import javax.activation.MailcapCommandMap; 
import javax.mail.BodyPart; 
import javax.mail.Message.RecipientType;
import javax.mail.Multipart; 
import javax.mail.PasswordAuthentication; 
import javax.mail.Session; 
import javax.mail.Transport; 
import javax.mail.internet.InternetAddress; 
import javax.mail.internet.MimeBodyPart; 
import javax.mail.internet.MimeMessage; 
import javax.mail.internet.MimeMultipart; 
 
/**
 * Mail is a predefined class provided by Google to create a socket connection and send the mail using SMTP
 * 
 * @author Srikanth Rao
 * 
 */
public class Mail extends javax.mail.Authenticator
{ 
  private String _user; 
  private String _pass; 
 
  private String[] _to; 
  private String _from; 
 
  private String _port; 
  private String _sport; 
 
  private String _host; 
 
  private String _subject; 
  private String _body; 
 
  private boolean _auth; 
   
  private boolean _debuggable; 
 
  private Multipart _multipart; 
 
 
  /**
   * Construtor to configure the Main client
   */
  public Mail()
  { 
    _host = "smtp.gmail.com"; // default smtp server 
    _port = "465"; // default smtp port 
    _sport = "465"; // default socketfactory port 
 
    _user = ""; // username 
    _pass = ""; // password 
    _from = ""; // email sent from 
    _subject = ""; // email subject 
    _body = ""; // email body 
 
    _debuggable = false; // debug mode on or off - default off 
    _auth = true; // smtp authentication - default on 
 
    _multipart = new MimeMultipart(); 
 
    // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added. 
    MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
    mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
    mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
    mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
    mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
    mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822"); 
    CommandMap.setDefaultCommandMap(mc); 
  } 
 
  /**
   * Mail Constructor with arguments that takes in username and password strings
   * @param user String variable for username
   * @param pass String variable for password
   */
  public Mail(String user, String pass)
  { 
    this(); 
 
    _user = user; 
    _pass = pass; 
  } 
 
  /**
   * Send is a final method of type boolean which sends the email and returns a boolean value whether the mail is sent or not
   * @return Boolean value telling whether mail sending was success or failure
   * @throws Exception Exceptions like No Internet Connectivity or Mail attachment i.e File I/O Exceptions
   */
  public boolean send() throws Exception
  { 
    Properties props = _setProperties(); 
 
    if(!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("") && !_body.equals(""))
    { 
      Session session = Session.getInstance(props, this); 
 
      MimeMessage msg = new MimeMessage(session); 
 
      msg.setFrom(new InternetAddress(_from)); 
       
      InternetAddress[] addressTo = new InternetAddress[_to.length]; 
      for (int i = 0; i < _to.length; i++)
      { 
        addressTo[i] = new InternetAddress(_to[i]); 
      } 
        msg.setRecipients(RecipientType.TO, addressTo); 
 
      msg.setSubject(_subject); 
      msg.setSentDate(new Date()); 
 
      // setup message body 
      BodyPart messageBodyPart = new MimeBodyPart(); 
      messageBodyPart.setText(_body); 
      _multipart.addBodyPart(messageBodyPart); 
 
      // Put parts in message 
      msg.setContent(_multipart); 
 
      // send email 
      Transport.send(msg); 
 
      return true; 
    }
    else
    { 
      return false; 
    } 
  } 
 
  /**
   * Method to add attachments to the email to be sent
   * @param filename file which is to be attached in the mail
   * @throws Exception File I/O Exceptions handler
   */
  public void addAttachment(String filename) throws Exception
  { 
    BodyPart messageBodyPart = new MimeBodyPart(); 
    DataSource source = new FileDataSource(filename); 
    messageBodyPart.setDataHandler(new DataHandler(source)); 
    messageBodyPart.setFileName(filename); 
 
    _multipart.addBodyPart(messageBodyPart); 
  } 
 
  /**
   * Method used to authenticate the username and password for the email
   */
  @Override 
  public PasswordAuthentication getPasswordAuthentication()
  { 
    return new PasswordAuthentication(_user, _pass); 
  } 
 
  private Properties _setProperties()
  { 
    Properties props = new Properties(); 
 
    props.put("mail.smtp.host", _host); 
 
    if(_debuggable)
    { 
      props.put("mail.debug", "true"); 
    } 
 
    if(_auth)
    { 
      props.put("mail.smtp.auth", "true"); 
    } 
 
    props.put("mail.smtp.port", _port); 
    props.put("mail.smtp.socketFactory.port", _sport); 
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
    props.put("mail.smtp.socketFactory.fallback", "false"); 
 
    return props; 
  } 
 
  /**
   * Getter method for Mail body
   * @return String value for mail body
   */
  public String getBody()
  { 
    return _body; 
  } 
 
  /**
   * Setter method for Mail body
   */
  public void setBody(String _body)
  { 
    this._body = _body; 
  } 
  
  /**
   * Setter method for toAddress of the Mail
   * @param toArr Email address value to which the mail will be sent
   */
  public void setTo(String[] toArr)
  {
	  this._to = toArr;
  }

  /**
   * Setter method for setting From address of the Mail
   * @param string Email address value from which the mail is sent
   */
  public void setFrom(String string)
  {
	  this._from = string;
  }

  /**
   * Setter method to set the Mail Subject
   * @param string String value for Subject in the Mail
   */
  public void setSubject(String string)
  {
	  this._subject = string;
  } 
 
} 