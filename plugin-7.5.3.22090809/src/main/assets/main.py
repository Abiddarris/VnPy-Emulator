from __future__ import print_function

import os
import sys

def main() :
    game_path = os.environ.get('ANDROID_PUBLIC')
    game_script = os.environ.get('GAME_SCRIPT')

    if not game_path or not game_script:
        print("Error: 'ANDROID_PUBLIC' or 'GAME_SCRIPT' environment variable is not set.")
        return

    if not os.path.isdir(game_path):
        print("Error: The directory " + game_path + " does not exist.")
        return

    game_script = os.path.splitext(game_script)[0]
    
    sys.path.insert(0, '')
    os.chdir(game_path)

    try:
        script = __import__(game_script)
    except ImportError as e:
        print("Error: Unable to import the script " + game_path)
        return

    if not hasattr(script, 'main'):
        print("Error: The script " + game_script + " does not have a 'main' function.")
        return

    script.main()
    
if (__name__ == "__main__") :
    main()    
