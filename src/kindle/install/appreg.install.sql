INSERT OR IGNORE INTO "handlerIds" VALUES('uk.co.notori.gol');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.notori.gol','lipcId','uk.co.notori.gol');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.notori.gol','jar','/opt/amazon/ebook/booklet/GameOfLife.jar');

INSERT OR IGNORE INTO "properties" VALUES('uk.co.notori.gol','maxUnloadTime','45');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.notori.gol','maxGoTime','60');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.notori.gol','maxPauseTime','60');

INSERT OR IGNORE INTO "properties" VALUES('uk.co.notori.gol','default-chrome-style','NH');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.notori.gol','unloadPolicy','unloadOnPause');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.notori.gol','extend-start','Y');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.notori.gol','searchbar-mode','transient');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.notori.gol','supportedOrientation','U');

INSERT OR IGNORE INTO "mimetypes" VALUES('gol','MT:image/x.gol');
INSERT OR IGNORE INTO "extenstions" VALUES('gol','MT:image/x.gol');
INSERT OR IGNORE INTO "properties" VALUES('archive.displaytags.mimetypes','image/x.gol','GameOfLife');
INSERT OR IGNORE INTO "associations" VALUES('com.lab126.generic.extractor','extractor','GL:*.gol','true');
INSERT OR IGNORE INTO "associations" VALUES('uk.co.notori.gol','application','MT:image/x.gol','true');
