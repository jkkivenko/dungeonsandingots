# Curveball. This is a python file!

import os
import sys

def gen_item():
    item_name = input("code-readable fully unique item_name: ")
    translated_name = input("Translated Capitalized Item Name: ")

    # Generate item entry
    with open("james_datagen/items/template.json", 'r') as f:
        template = f.read()
        edited_template = template.replace("ITEM_NAME", item_name)
        with open(f"src/main/resources/assets/dungeonsandingots/items/{item_name}.json", 'w') as dest_file:
            dest_file.write(edited_template)
    # Generate item model
    with open("james_datagen/models/item/template.json", 'r') as f:
        template = f.read()
        edited_template = template.replace("ITEM_NAME", item_name)
        with open(f"src/main/resources/assets/dungeonsandingots/models/item/{item_name}.json", 'w') as dest_file:
            dest_file.write(edited_template)
    # Generate translation entry
    with open("src/main/resources/assets/dungeonsandingots/lang/en_us.json", 'r') as translation_file:
        file_contents = translation_file.read()
        edited_contents = file_contents.replace("\n}", f",\n  \"item.dungeonsandingots.{item_name}\": \"{translated_name}\"\n}}")
    with open("src/main/resources/assets/dungeonsandingots/lang/en_us.json", 'w') as translation_file:
        translation_file.write(edited_contents)
    print("Generated item entry, model, and translation")
    print("Don't forget to update ModItems, add the texture file, and update the creative mode tab!")

def gen_block():
    block_name = input("code-readable fully unique block_name: ")
    translated_name = input("Translated Capitalized Block Name: ")

    # Generate blockstates file
    with open("james_datagen/blockstates/template.json", 'r') as f:
        template = f.read()
        edited_template = template.replace("BLOCK_NAME", block_name)
        with open(f"src/main/resources/assets/dungeonsandingots/blockstates/{block_name}.json", 'w') as dest_file:
            dest_file.write(edited_template)
    # Generate item entry
    with open("james_datagen/items/template.json", 'r') as f:
        template = f.read()
        edited_template = template.replace("ITEM_NAME", block_name)
        with open(f"src/main/resources/assets/dungeonsandingots/items/{block_name}.json", 'w') as dest_file:
            dest_file.write(edited_template)
    # Generate block model
    with open("james_datagen/models/block/template.json", 'r') as f:
        template = f.read()
        edited_template = template.replace("BLOCK_NAME", block_name)
        with open(f"src/main/resources/assets/dungeonsandingots/models/block/{block_name}.json", 'w') as dest_file:
            dest_file.write(edited_template)
    # Generate item model
    with open("james_datagen/models/item/block_item_template.json", 'r') as f:
        template = f.read()
        edited_template = template.replace("BLOCK_NAME", block_name)
        with open(f"src/main/resources/assets/dungeonsandingots/models/item/{block_name}.json", 'w') as dest_file:
            dest_file.write(edited_template)
    # Generate translation entry
    with open("src/main/resources/assets/dungeonsandingots/lang/en_us.json", 'r') as translation_file:
        file_contents = translation_file.read()
        edited_contents = file_contents.replace("\n}", f",\n  \"block.dungeonsandingots.{block_name}\": \"{translated_name}\"\n}}")
    with open("src/main/resources/assets/dungeonsandingots/lang/en_us.json", 'w') as translation_file:
        translation_file.write(edited_contents)
    print("Generated blockstate, item entry, block model, item model, and translation entry.")
    print("Don't forget to update ModBlocks and ModItems, add the texture file, and update the creative mode tab!")
    
    


if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "item":
        gen_item()
    elif len(sys.argv) > 1 and sys.argv[1] == "block":
        gen_block()
    else:
        print("USAGE: python3 gen.py item OR python3 gen.py block")