DELETE FROM "handlerIds" WHERE handlerId='uk.co.notori.gol';
DELETE FROM "properties" WHERE handlerId='uk.co.notori.gol';
DELETE FROM "associations" WHERE handlerId='uk.co.notori.gol';

DELETE FROM "mimetypes" WHERE ext='gol';
DELETE FROM "extenstions" WHERE ext='gol';
DELETE FROM "properties" WHERE value='GameOfLife';
DELETE FROM "associations" WHERE contentId='GL:*.gol';
