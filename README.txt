     _                                __     __             _            ____       _   _   _                  _        _   
    / \   _ __ ___   __ _ _______  _ _\ \   / /__ _ __   __| | ___  _ __/ ___|  ___| |_| |_(_)_ __   __ _ ___ | |___  _| |_ 
   / _ \ | '_ ` _ \ / _` |_  / _ \| '_ \ \ / / _ \ '_ \ / _` |/ _ \| '__\___ \ / _ \ __| __| | '_ \ / _` / __|| __\ \/ / __|
  / ___ \| | | | | | (_| |/ / (_) | | | \ V /  __/ | | | (_| | (_) | |   ___) |  __/ |_| |_| | | | | (_| \__ \| |_ >  <| |_ 
 /_/   \_\_| |_| |_|\__,_/___\___/|_| |_|\_/ \___|_| |_|\__,_|\___/|_|  |____/ \___|\__|\__|_|_| |_|\__, |___(_)__/_/\_\\__|
                                                                                                    |___/                   
Il file AmazonVendorConnectionSettings.txt deve contenere i seguenti dati formattati nel modo seguente:
- la prima riga contiene il nome utente per la connessione in download
- la seconda riga contiene il percorso del file che contiene la chiave privata per la connessione in download
- la terza riga contiene il nome utente per la connessione in upload
- la quarta riga contiene il percorso del file che contiene la chiave privata per la connessione in upload
La passphrase, per motivi di sicurezza, non è contenuta nel file delle impostazioni né nel programma. Deve essere invece inserita dall'utente nel momento della esecuzione. Il programma richiede una sola passphrase, la passphrase attuale per la chiave in download dovrà essere la passphrase inserita dall'utente seguita da "D" e la passphrase attuale usata per la chiave in upload dovrà essere la passphrase inserita dall'utente seguita da "U".
Il nome del server e la porta della connesione sono hard-coded nel programma e non possono essere modificate nel file delle impostazioni.