# Java library for exchanging messages with Amazon Vendor using SFTP
A small Java library that implements the sending and receiving of messages to and from the Amazon Vendor servers, using the SFTP protocol as specified in the document "Amazon SFTP Guide".

## Index
* [Release notes](#release-notes)
* [Introduction](#introduction)
* [The connection settings file](#the-connection-settings-file)
* [Documentation](#documentation)
  * [MessageExchanger](#messageexchanger)
  * [SFTPBasedMessageExchangerForAmazonVendor](#sftpbasedmessageexchangerforamazonvendor)
  * [AmazonVendorSFTPConnectionValidator](#amazonvendorsftpconnectionvalidator)
  * [ConnectionException](#connectionexception)
  * [MessageForwardingException](#messageforwardingexception)
  * [MessageReceptionException](#messagereceptionexception)
* [Conclusion](#conclusion)

## Release notes
* **1.0.1**: Renamed package: "it.trv.vendorsftpconnection" -> "it.trvi.vendorsftpconnection"
* **1.0.0**: Initial release

## Introduction
The objective of this package is to provide a simple tool to exchange messages with the Amazon Vendor servers using the SFTP protocol. It sends and receives the messages as raw text, so it's up to the users of this library to ensure the content of the messages is consistent and processed correctly. It uses the JSch library by JCraft to create and manage the actual SFTP sessions.

Knowledge of the Amazon Vendor documentation is needed to understand and use this tool and this documentation correctly. This package is a way to make the process of establishing a working connection with Amazon's SFTP servers faster, but knowing and understanding how this connection is set up and how it works is still necessary. The document "Amazon SFTP Guide" is the one that requires the most attention for the purposes of this package. If you need help creating the SSH keys, I suggest you follow [this guide](https://www.ssh.com/ssh/keygen/).

As stated in the license, this software is provided "as is" without warranty of any kind. I'm not responsible for any issue and/or damage that this software may cause. Please read the full license for more legal jargon. This package has undergone limited testing, because I have no longer access to an Amazon Vendor account; please report any error or problem you might find, and I'll try to fix it to the best of my ability.

All the classes in this library are contained in the package "`it.trvi.vendorsftpconnection`".

## The connection settings file
This file contains some of the information necessary to create a new session with the SFTP servers: the path to this file is one of the parameters that must be specified when establishing a connection. It contains the following data:
* the first line contains the username for the download connection
* the second line contains the path to the file that contains the private key for the download connection
* the third line contains the username for the upload connection
* the fourth line contains the path to the file that contains the private key for the upload connection

The content of the settings file should look similar to this:
```
KWNFGL9Q0ZXN
settings_and_keys/privateKeyDownload
SKQMDK7Q3FHB
settings_and_keys/privateKeyUpload
```

For obvious security reasons, the passphrases are not kept in this file.

## Documentation

### MessageExchanger
An **abstract class** that defines a simple tool to send and receive String messages. Implements `AutoCloseable`.

#### void send(String msg)
Sends a message.

Parameters:  
&ensp;&ensp;`msg` contains the message to be sent.  
Throws:  
&ensp;&ensp;`MessageForwardingException` if an error prevents the sending of the message.

#### String receive()
If there's a message waiting to be received, it immediately returns it. Otherwise, it throws a `NoSuchElemetException`.

Return:  
&ensp;&ensp;the received message.  
Throws:  
&ensp;&ensp;`MessageReceptionException` if an error prevents the sending of the message.  
&ensp;&ensp;`NoSuchElementException` if there's no message waiting to be received.

#### String waitForMessage(int waitTime)
If there's a message waiting to be received, it immediately returns it. Otherwise, it waits until such a message exists, then it returns it. It tries to receive a message every `waitTime` milliseconds.

Parameters:  
&ensp;&ensp;`waitTime` is the time, in milliseconds, that it must wait between each reception attempt.  
Return:  
&ensp;&ensp;the received message.  
Throws:  
&ensp;&ensp;`MessageReceptionException` if an error prevents the receiving of the message.

#### String waitForMessageForMilliseconds(int totalWaitTime, int waitTime)
If there's a message waiting to be received, it immediately returns it. Otherwise, it waits at most `totalWaitTime` milliseconds for a message to arrive. It tries to receive a message every `waitTime` milliseconds. If the message arrives within `totalWaitTime` milliseconds, then it returns it as soon as it receives it, otherwise it throws a `TimeoutException`.

Parameters:  
&ensp;&ensp;`totalWaitTime` is the maximum amount of milliseconds that must be waited. If it's not a multiple of `waitTime`, it's approximated to the following multiple of `waitTime`.  
&ensp;&ensp;`waitTime` is the time, in milliseconds, that it must wait between each reception attempt.  
Return:  
&ensp;&ensp;the received message.  
Throws:  
&ensp;&ensp;`MessageReceptionException` if an error prevents the receiving of the message.  
&ensp;&ensp;`TimeoutException` if it doesn't receive a message within `totalWaitTime` milliseconds (approximated as explained above).

#### void close()
If the underlying communication is connection-oriented, it closes the connection. Otherwise it does nothing.  
If the connection is already closed, it does nothing.

### SFTPBasedMessageExchangerForAmazonVendor
A `Message Exchanger` used to send and receive messages to and from the Amazon Vendor server through the SFTP protocol. Extends `MessageExchanger`.  
Here I show only the methods that are not inherited from `MessageExchanger`.

#### SFTPBasedMessageExchangerForAmazonVendor(String settingsFilePath, String passphraseDown, String passphraseUp)
It creates a new `SFTPBasedMessageExchangerForAmazonVendor` and establishes an upload and download connection with the Amazon server using the settings contained in the file in `settingsFilePath` and the default host and port for Europe.

Parameters:  
&ensp;&ensp;`settingsFilePath` is the path to the settings file.  
&ensp;&ensp;`passphraseDown` is the passphrase for the private keys for the download connection.  
&ensp;&ensp;`passphraseUp` is the passphrase for the private keys for the upload connection.  
Throws:  
&ensp;&ensp;`ConnectionException` if an error occurs while establishing the connection.  
&ensp;&ensp;`IOException` if an error occurs while reading the settings file.

#### SFTPBasedMessageExchangerForAmazonVendor(String settingsFilePath, String passphraseDown, String passphraseUp, String host, int port)
It creates a new `SFTPBasedMessageExchangerForAmazonVendor` and establishes an upload and download connection with the Amazon server using the settings contained in the file in `settingsFilePath` and the specified address and port of the SFTP server.

Parameters:  
&ensp;&ensp;`settingsFilePath` is the path to the settings file.  
&ensp;&ensp;`passphraseDown` is the passphrase for the private keys for the download connection.  
&ensp;&ensp;`passphraseUp` is the passphrase for the private keys for the upload connection.  
&ensp;&ensp;`host` is the address of the SFTP server.  
&ensp;&ensp;`port` is the port of the SFTP server.  
Throws:  
&ensp;&ensp;`ConnectionException` if an error occurs while establishing the connection.  
&ensp;&ensp;`IOException` if an error occurs while reading the settings file.

#### send(String msg, String destinationFileName)
Modification of `send()` in which the name of the file uploaded to the remote server can be chosen, useful only for testing.

### AmazonVendorSFTPConnectionValidator
A class containing some static functions that automatically perform the validation of new SFTP connections, as required by Amazon.

#### static void main(String[] args)
Validates a new SFTP connection with the Amazon Vendor server.  
It can receive zero, one or three arguments:
* The first argument is the path of the settings file.
* The second and the third arguments are, respectively, the address and port of Amazon's SFTP server.

If it receives only two arguments, it ignores the second one and behaves as if it only received one.  
If it receives no arguments, it uses the default path for the settings file: "*settings_and_keys/AmazonVendorConnectionSettings.txt*".  
A popup will appear asking for the passphrases.

#### static void validateConnection(String settingsFilePath)
It validates a new SFTP connection with the Amazon Vendor server, by passing the tests for the connection required by Amazon.  
It uses the settings contained in the file in `settingsFilePath` and the default address and port for Europe.  
A popup will appear asking for the passphrases.

#### static void validateConnection(String settingsFilePath, String host, int port)
It validates a new SFTP connection with the Amazon Vendor server, by passing the tests for the connection required by Amazon.  
It uses the settings contained in the file in `settingsFilePath` and the address and port specified in, respectively, `host` and `port`.  
A popup will appear asking for the passphrases.

### ConnectionException
A simple exception that is thrown when a connection cannot be established.

### MessageForwardingException
A simple exception that is thrown when a message cannot be sent.

### MessageReceptionException
A simple exception that is thrown when a message cannot be received because of some error.

## Conclusion
Please contact me if you are having problems specific to this library or find any error in the code or in the documentation. Suggestions are also very welcome.

If you are feeling generous, consider making a donation using one of the methods listed at the end of this document.

*Stefano Travasci*

---

Paypal: [![Donate](https://www.paypalobjects.com/en_US/IT/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/donate?hosted_button_id=9HMMFAZE248VN)

Curecoin: B7CwvrRKzyLFckfZjeY7d1ex7WcRGhrToK

Bitcoin: bc1qlyvpkva0rhut4cvwmvna9py765w9tu4603qllt

<sub>*let me know if your favorite donation method is not in this list*</sub>
