<?php
	date_default_timezone_set('Etc/UTC');
	require 'PHPMailer-master/PHPMailerAutoload.php';
	ob_start();

	session_start();
	include("connect.php");

	$email = $_POST['user_email'];
	


	//Get password from customer_login_details table
	$result= pg_query($db,"SELECT * from  customer_login_details WHERE email = '$email' LIMIT 1 " );

    $rows = pg_num_rows($result);
	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))   
		{		
			
			$username=$row['email'];
			$password=$row['password'];
		}

		$to =$email;
			
		//Amazon SES and PhpMailer integration

		// Replace sender@example.com with your "From" address.
		// This address must be verified with Amazon SES.
		$sender = 'forgot@tagid.co.in';
		$senderName = 'TagId';

		// Replace recipient@example.com with a "To" address. If your account
		// is still in the sandbox, this address must be verified.
		$recipient = $to;

		// Replace smtp_username with your Amazon SES SMTP user name.
		$usernameSmtp = 'AKIAWG3BGKSUBUHGKR3M';

		// Replace smtp_password with your Amazon SES SMTP password.
		$passwordSmtp = 'BN4xFDewFrwc/TdseYK4r8d22l9dP/LksXmOAk3S3OSu';

		// Specify a configuration set. If you do not want to use a configuration
		// set, comment or remove the next line.
		//$configurationSet = 'ConfigSet';

		// If you're using Amazon SES in a region other than US West (Oregon),
		// replace email-smtp.us-west-2.amazonaws.com with the Amazon SES SMTP
		// endpoint in the appropriate region.
		$host = 'email-smtp.ap-south-1.amazonaws.com';
		$port = 587;

		// The subject line of the email
		$subject = 'Login credentials of TagSmart';

		// The plain-text body of the email
		
		$bodyText = "User name: ".$email." \n";
		$bodyText .= "Password: ". base64_decode ($password);

		// The HTML-formatted body of the email
		$bodyHtml = '<table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
                <td align="center" bgcolor="#f2eaed" style="padding: 40px 0 30px 0; color: #fff; font-size: 28px; font-weight: bold; font-family: Arial, sans-serif;">
                    <img src="http://tagid.co.in/images/tag-id-solutions-logo.png"  width="300" height="60" style="display: block;" />
					<h3 style="color:#16235A;">TagId Solutions Pvt Ltd</h3>
                </td>
            </tr>
			<tr style="padding: 40px 0 30px 0; color: #000000;  font-weight: bold; font-family: Arial, sans-serif;">
                <td align="left" >
                   	<p>Username:   </p><h2><b>'.$email.'</b></h2>
                </td>
                
            </tr>
            <tr style="padding: 40px 0 30px 0; color: #000000;font-weight: bold; font-family: Arial, sans-serif;">
                <td align="left" >
                   	<p>Password: </p><h2><b>'.base64_decode ($password).'</b></h2>
                </td>
                
            </tr>

            <tr>
                <td align="left" >
                   	<br><br><b>Warm Regards,</b><br><br>
                   	Team TagId<br>
                   	<a href="http://tagid.co.in/"><b>TagId solutions private limited</b></a><br><br>
                </td>
            </tr>
            
                        
            </table>';
				

				$mail = new PHPMailer;

				try 
				{
				    // Specify the SMTP settings.
				    $mail->isSMTP();
				    $mail->setFrom($sender, $senderName);
				    $mail->Username   = $usernameSmtp;
				    $mail->Password   = $passwordSmtp;
				    $mail->Host       = $host;
				    $mail->Port       = $port;
				    $mail->SMTPAuth   = true;
				    $mail->SMTPSecure = 'tls';
				    //$mail->addCustomHeader('X-SES-CONFIGURATION-SET', $configurationSet);

				    // Specify the message recipients.
				    $mail->addAddress($recipient);
				    // You can also add CC, BCC, and additional To recipients here.

				    // Specify the content of the message.
				    $mail->isHTML(true);
				    $mail->Subject    = $subject;
				    $mail->Body       = $bodyHtml;
				    $mail->AltBody    = $bodyText;
			
					if ($mail->send()) 
					{
						$response = array('status' => '1','msg' => 'Updated Successfully');
		

						echo json_encode($response);
					}else	
					{	
					
						$response = array('status' => '0','msg' => 'Something went wrong. Try Again!');		

						echo json_encode($response);($responses);
					}
				} catch (phpmailerException $e) {
						$response = array('status' => '0','msg' => 'Something went wrong. Try Again!');		

						echo json_encode($response);($responses);
				} catch (Exception $e) {
				    
				    
					$response = array('status' => '0','msg' => 'Something went wrong. Try Again!');		

					echo json_encode($response);($responses);
				}
				
				
	}

	
	
	
?>