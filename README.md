# adnoc-sap-commercecloud local setup

**Create the Directory**:
>Create a folder named c:\workdrive\adnocb2b. We'll refer to it as `<setupdir>`.

**Place the Zip Packages:**
>Place the zip packages of SAP Commerce and SAP Integration in `<setupdir>`.

**First Command Prompt(Run as Administrator):**

>Open a command prompt in c:\workdrive\adnocb2b.

>Execute the following commands:

```
"c:\Program Files\7-Zip\7z.exe" x CXCOMCL221100U_44-70007431.ZIP
"c:\Program Files\7-Zip\7z.exe" x CXCOMIEP221100U_38-70007891.ZIP
Choose "Yes" to replace files if prompted.
```
MacOs
```
unzip CXCOMCL221100U_44-70007431.ZIP
unzip CXCOMIEP221100U_38-70007891.ZIP
```

>Delete CXCOMCL221100U_44-70007431.ZIP and CXCOMIEP221100U_38-70007891.ZIP.
```
del CXCOMCL221100U_44-70007431.ZIP
del CXCOMIEP221100U_38-70007891.ZIP
```

MacOs
```
rm CXCOMCL221100U_44-70007431.ZIP
rm CXCOMCL221100U_44-70007431.ZIP
```

**Second Command Prompt:**

>Open a second command prompt in `<setupdir>\hybris\bin\platform`.

>Execute `setantenv.bat` or `. ./setantenv.sh` and then run `ant`.

>Press `Enter` when prompted for [develop].

**Back to First Command Prompt:**

>Delete `hybris\config\localextensions.xml` and `hybris\config\local.properties` files:
```
del hybris\config\localextensions.xml
del hybris\config\local.properties
```

MacOs
```
rm hybris/config/localextensions.xml
rm hybris/config/local.properties
```

**Git Repository:**
>Clone the adnoc-sap-commercecloud Git repository into c:\workdrive\gitRepo:
```
git config --system core.longpaths true
git config --global http.sslBackend schannel
git clone https://devopsad.visualstudio.com/META%20Transformation%20Program/_git/META%20adnoc-sap-commercecloud c:\workdrive\gitRepo\Meta
```
**Symbolic Links:**

>Create symbolic links:
```
mklink manifest.json C:\workdrive\gitRepo\Meta\adnoc-sap-commercecloud\core-customize\manifest.json
mklink /J js-storefront C:\workdrive\gitRepo\Meta\adnoc-sap-commercecloud\js-storefront
mklink /J hybris\bin\custom C:\workdrive\gitRepo\Meta\adnoc-sap-commercecloud\core-customize\hybris\bin\custom
mklink hybris\config\local.properties C:\workdrive\gitRepo\Meta\adnoc-sap-commercecloud\core-customize\hybris\config\local.properties
mklink hybris\config\localextensions.xml C:\workdrive\gitRepo\Meta\adnoc-sap-commercecloud\core-customize\hybris\config\localextensions.xml
mklink /J hybris\config\environments C:\workdrive\gitRepo\Meta\adnoc-sap-commercecloud\core-customize\hybris\config\environments
```

MacOs
```
# be in your project root
cd /Users/Anoop.Kumar/workdrive/adnocb2b3934

# create symlinks (TARGET then LINK)
ln -s /Users/Anoop.Kumar/workdrive/gitRepo/Meta/adnoc-sap-commercecloud/core-customize/manifest.json manifest.json
ln -s /Users/Anoop.Kumar/workdrive/gitRepo/Meta/adnoc-sap-commercecloud/js-storefront js-storefront
ln -s /Users/Anoop.Kumar/workdrive/gitRepo/Meta/adnoc-sap-commercecloud/core-customize/hybris/bin/custom hybris/bin/custom
ln -s /Users/Anoop.Kumar/workdrive/gitRepo/Meta/adnoc-sap-commercecloud/core-customize/hybris/config/local.properties hybris/config/local.properties
ln -s /Users/Anoop.Kumar/workdrive/gitRepo/Meta/adnoc-sap-commercecloud/core-customize/hybris/config/localextensions.xml hybris/config/localextensions.xml
ln -s /Users/Anoop.Kumar/workdrive/gitRepo/Meta/adnoc-sap-commercecloud/core-customize/hybris/config/environments hybris/config/environments
```

>Execute in Second Command Prompt:

"C:\Program Files\SapMachine\JDK\17\bin\keytool.exe" -importcert -trustcacerts -file "C:\workdrive\Adnoc\zscaler_root.crt" -alias zscaler-root -keystore "C:\Program Files\SapMachine\JDK\17\lib\security\cacerts" -storepass changeit

Change setantenv.sh to replace line with below:
export ANT_OPTS="$MEM_OPTS -Dfile.encoding=UTF-8 -Dpolyglot.js.nashorn-compat=true -Dpolyglot.engine.WarnInterpreterOnly=false \
--add-exports java.xml/com.sun.org.apache.xpath.internal=ALL-UNNAMED \
--add-exports java.xml/com.sun.org.apache.xpath.internal.objects=ALL-UNNAMED \
-Djava.library.path=$PLATFORM_HOME/../modules/sap-framework-core/sapcorejco/lib/darwinarm64"



>Run the following commands:
```
ant clean all
ant initialize > C:\workdrive\Adnoc\adnoc_initialize.txt 2>&1
ant updatesystem > C:\workdrive\Adnoc\adnoc_update.txt 2>&1
hybrisserver.bat debug > C:\workdrive\Adnoc\adnoc_serverstart.txt 2>&1
```

MacOs
```
ant initialize > /Users/Anoop.Kumar/workdrive/Adnoc/adnoc_initialize.txt 2>&1
ant updatesystem > /Users/Anoop.Kumar/workdrive/Adnoc/adnoc_update.txt 2>&1
```

**Open hac and execute following impex:**

>Trigger
```
	INSERT_UPDATE Trigger;cronJob(code)[unique=true];second;minute;hour;day;month;year;relative;active;maxAcceptableDelay
		;update-adnocIndex-cronJob;0;1;-1;-1;-1;-1;true;false;-1
		;update-backofficeIndex-CronJob;0;1;-1;-1;-1;-1;true;false;-1
```

**Login:**

| Role  						|                                                                        UserId 						                                                                         | Password |
| ------------- |:------------------------------------------------------------------------------------------------------------------------------------------------------------:| ------------- |
| b2bcustomer      			|                       300000.customer@adnocdistribution.ae, 300001.customer@adnocdistribution.ae, 300002.customer@adnocdistribution.ae                       | 12341234 |
| b2badmin      				|                                                            100000.admin@adnocdistribution.ae     	                                                            | 12341234 |

### For spartacustorefront start:

>change API_BASE_URL to "https://localhost:9002" in file `C:\workdrive\gitRepo\adnoc-sap-commercecloud\js-storefront\b2bspastore\src\environment\environmnet.ts`

>Open command prompt at <setupdir>\js-storefront\b2bspastore like `C:\workdrive\gitRepo\adnoc-sap-commercecloud\js-storefront\b2bspastore`

and execute
```
npm install
npm start
```

>d1 admin credentails:
`admin/V!j7Z9y&MMk1{ZeUTwnsZ@Nkr`
