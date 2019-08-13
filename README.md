# Asset-Cache
The standalone cache API

If you're looking for the application tool to manage cache look [HERE](https://github.com/DTanJP/Asset-Cache-Packer)

There are 2 different versions of the cache class along with demonstrations of using the cache class

# Standard version
### Only includes loading the cache and it's contents into memory and cannot do anything else. This is the ideal version for game/application development

# Full version
### Includes loading, extracting files, packing, and adding/removing files to the cache. This version is if you want to create your own asset packer tool by yourself from scratch.

*Note: For anyone using this, I strongly recommend that you add encryption or change the cache model layout to your cache files to prevent other people with this API/tool from simply reading your cache files and ripping your assets out.

Running either demos should show this: (Demo.cache is required to run the demos)
![Demo screenshot](https://raw.githubusercontent.com/DTanJP/Asset-Cache/master/images/Screenshot_1.png)
