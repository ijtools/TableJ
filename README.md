TableJ is a plugin for Fiji/ImageJ that provides management of data tables in a more extensive 
way than native ImageJ does.

Features of TableJ include:
* management of Tables with both numeric and categorical columns
* display of Table instances in specific frame
* provide several operations on Tables from frame menu: display table info / summary, select columns...
* provide several plotting facilities: line plot, scatter plot...
* import and export from CV files

## Installation

Copy the "[TableJ.x.y.z.jar](https://github.com/ijtools/TableJ/releases/download/v0.0.1/TableJ_-0.0.1.jar)" into the plugin directory of your ImageJ/Fiji installation 
(update version numbers according to latest release), and restart Fiji.
For plotting features, you need to download a recent version of [the xchart library](https://knowm.org/open-source/xchart/) as well.

## Usage

After installation, new menu entries appear within the "Plugins -> IJ Tools -> Table" menu:
* **Convert Results** Table allows for converting an existing Table from ImageJ into a Table instance within TableJ
* **Import Table** Imports a new Table from a csv file
* **Open Demo Table** Opens a simple data table (Fisher's iris) for testing purpose.
