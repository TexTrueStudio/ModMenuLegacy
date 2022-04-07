import json
from pathlib import Path


for file in Path( '.' ).glob('*.json'):
	lang: dict[str, str] = json.loads( file.read_text(encoding='utf-8') )
	langFile = ''
	
	for key, value in lang.items():
		if 'drop' not in key:
			langFile += f'{key}={value}\n'
	
	( file.parent / ( file.name.removesuffix('json') + 'lang' ) ).write_text( langFile, encoding='utf-8' )
