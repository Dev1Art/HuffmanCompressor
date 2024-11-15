# Huffman Compressor
<img alt="GitHub Created At" src="https://img.shields.io/github/created-at/Dev1Art/huffman-compressor"> <img alt="GitHub License" src="https://img.shields.io/github/license/Dev1Art/huffman-compressor"> <img alt="GitHub top language" src="https://img.shields.io/github/languages/top/Dev1Art/huffman-compressor"> <img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/Dev1Art/huffman-compressor">
<br/>
#### This is a simple tool written in Java for compressing effectively file directories using Huffman approach. Gui made with Swing and improved with FlatLaf.
## Description
#### The application is a graphical interface that allows to work with file directories including the possibility:
- Compress directories using Huffman coding.
- Decompress previously compressed directories.
- Command-line interface for easy use.
## Technology
#### The following technologies were used in the project:
- Java 17
- Swing API
- FlatLaf 3.1.1
## Requirements
- **Java**: This application requires Java JDK 8 or higher. Ensure you have it installed by running:
  ```bash
  java -version
  ```
- **Maven**: (Optional) If you wish to build the project from source, Maven is recommended for dependency management.
## Installation
### 1. Clone the Repository
You can clone the repository to your local machine using Git. Open a terminal and run:
  ```bash
git clone https://github.com/Dev1Art/huffman-compressor.git
cd huffman-compressor
```
### 2. Build the Project
If you have Maven installed, you can build the project by running:
  ```bash
mvn clean package
```
This will compile the project and create a JAR file in the target directory.
### 3. Directly Download JAR (Optional)
If you prefer not to build the project yourself, you can download the latest JAR file from the target directory after building, or you can provide a link to a pre-built JAR file if available.
## Usage
### 1. Command Line Interface
You can use the application via the command line. Here’s how:
#### Compressing a File
To compress a text file, use the following command:
  ```bash
java -jar target/huffman-compressor.jar compress <input_dir> <output_dir>
```
- <input_dir>: The path to the directory you want to compress.
- <output_dir>: The path where you want to save the compressed file .huff.
#### Decompressing a File
To decompress a previously compressed directory, run:
  ```bash
java -jar target/huffman-compressor.jar decompress <input_dir> <output_dir>
```
- <input_dir>: The path to the directory of the compressed file.
- <output_dir>: The path where you want to save the decompressed directory.
### 2. Tests
#### You can find some directories for in-place tests in test_cases zip archive. So before the tests you should unpack it. Here is the example how you can test it:
#### Compress:
  ```bash
java -jar target/huffman-compressor.jar compress BIG_PACK SAVES
```
#### Decompress:
  ```bash
java -jar target/huffman-compressor.jar decompress SAVES SAVES
```
#### Check that dir_to_decompressed matches the content of dir_to_compress.
## Credits
#### Thanks to DanielDFY for his Huffman implementation that I used in my project. His work at https://github.com/DanielDFY/Huffman.
## Contributing
Contributions are welcome! If you would like to contribute, please fork the repository and make a pull request. For major changes, please open an issue first to discuss what you would like to change.
## License
This project is licensed under the MIT License - see the LICENSE file for details.





