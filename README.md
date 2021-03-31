# Java library for exchanging messages with Amazon Vendor using SFTP
A small Java library that implements the sending and receiving of messages to and from the Amazon Vendor servers, using the SFTP protocol as specified in the document "Amazon SFTP Guide".

## Index
* [Introduction](#introduction)
* [The connection settings file](#the-connection-settings-file)
* [Documentation](#documentation)
* [Conclusion](#conclusion)

## Introduction
The objective of this package is to provide a simple tool to exchange messages with the Amazon Vendor servers using the SFTP protocol. It sends and receives the messages as raw text, so it's up to the users of this library to ensure the content of the messages is consistent and processed correctly. It uses the JSch library by JCraft to create and manage the actual SFTP sessions.

Knowledge of the Amazon Vendor documentation is needed to understand and use this tool and this documentation correctly. This package is a way to make the process of establishing a working connection with Amazon's SFTP servers faster, but knowing and understanding how this connection is set up and how it works is still necessary. The document "Amazon SFTP Guide" is the one that requires the most attention for the purposes of this package. If you need help creating the SSH keys, I suggest you follow [this guide](https://www.ssh.com/ssh/keygen/); please refer to the next paragraph for the rules you need to follow when choosing the passphrase.

As stated in the license, this software is provided "as is" without warranty of any kind. I'm not responsible for any issue and/or damage that this software may cause. Please read the full license for more legal jargon. This package has undergone limited testing, because I have no longer access to an Amazon Vendor account; please report any error or problem you might find, and I'll try to fix it to the best of my ability.

## The connection settings file

## Documentation

## Conclusion
