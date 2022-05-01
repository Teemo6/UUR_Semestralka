Návod pro použití s GIT pro úplné začátečníky
---------------------------------------------
1. Pokud již máte zřízen někde GIT účet umožňující vám zakládat soukromé (private) repozitáře, jděte na krok 3
2. Založte si účet na https://gitlab.com/ nebo https://bitbucket.org nebo https://github.com. 
3. Založte nový SOUKROMÝ (private) repozitář a nějak vhodně si ho pojmenujte. Rady, ať založíte .gitignore nebo soubor readme, ignorujte - vy již máte svůj existující projekt.
4. Získejte HTTPS adresu k vašemu repozitáři (bývá zřetelně uvedena).
5. Pokud jste tento projekt získali doporučeným klonováním z Git (máte zde skrytý podadresář .git), jděte na krok 8.
6. Prostřednictvím TortoiseGit (vyvolá se z kontextového menu v průzkumníkovi) nebo Git Extensions (či jiných) založte lokální repozitář v tomto adresáři (Git Create repository here ...). Od této chvíle můžete provádět "commit" a uchovávat lokálně změny.
7. Vyvolejte Git Commit a všechny soubory "commitujte" do lokálního repozitáře (počáteční/první commit).
8. Vyvolejte Push a v nastavení "Remote" přidejte nový vzdálený repozitář s názvem "origin" a jako URL volte tu, kterou jste získali v kroku 4 (tj. HTTPS adresu k vašemu repozitáři). Dokončete Push. TortoiseGit si během toho vyžádá vaše přihlašovací údaje a všechny vaše změny (lokálně vedené v podadresáři .git) zkopíruje do vzdáleného repozitáře.
8. Zkontrolujte, že data jsou skutečně uložena.

Poznámka: využijte soubor .gitignore pro specifikaci automaticky generovaných souborů (.class, javadoc dokumentace, apod.), aby se tyto soubory neukládaly do Git repozitářů.
