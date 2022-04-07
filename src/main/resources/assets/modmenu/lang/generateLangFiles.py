import json
from pathlib import Path


for file in Path( '.' ).glob('*.json'):
	lang = ''
	
	for key, value in json.loads( file.read_text( encoding='utf-8' ) ).items():
		if 'drop' not in key:
			lang += f'{key}={value}\n'
	
	( file.parent / ( file.name.removesuffix('json') + 'lang' ) ).write_text( lang, encoding='utf-8' )
