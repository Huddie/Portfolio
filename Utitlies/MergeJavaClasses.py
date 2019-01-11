import glob
import re
import os


import_set = set([])

def create_import_section(java_imports):
    imps = ""
    for imp in java_imports:
        imps += imp
    return imps

def line_prepender(filename, line):
    with open(filename, 'r+') as f:
        content = f.read()
        f.seek(0, 0)
        f.write(line.rstrip('\r') + content)

def add_import(java_import):
    import_set.add(java_import)

def get_all_filenames(path):
    return glob.glob(os.path.join(path, '*.java'))

def start():
    merged_filename = input("What is the name of your main method: ")
    fld_path = input("Please path containing java classes to merge: ")
    filenames = get_all_filenames(fld_path)
    merge_files(filenames, merged_filename, merged_filename)
    line_prepender(merged_filename + ".java", create_import_section(import_set))

def merge_files(filenames, merged_filename, main_class_name):
    with open(merged_filename + ".java",'w') as outfile:
        for f in filenames:
            with open(f) as infile:
                for line in infile:
                    found_import = re.search(r"^\s*import .*;", line)
                    found_class = re.search(r"^\s*public\s*(abstract)?\s*class.*", line)
                    found_interface = re.search(r"^\s*public\s*interface.*", line)
                    found_print = re.search(r"^\s*System.out.println", line)
                    if found_print:
                        continue
                    elif found_import:
                        add_import(line)
                    elif (found_class or found_interface) and main_class_name not in line:
                        line = line.replace('public', '')
                        outfile.write(line)
                    else:
                        outfile.write(line)

start()