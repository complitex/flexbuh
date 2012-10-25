import java.text.*

assert args.size() >= 1, 'Please specify version in N.N.N format'

def versionStr = args[0]

assert versionStr =~ /\d+.\d+.\d+/, 'Need to specify version as parameter in N.N.N format'

class Version implements Comparable {
	int number1
    int number2
    int number3

	int compareTo(Version v) {
		return number1 <=> v.number1 ?: number2 <=> v.number2 ?: number3 <=> v.number3
	}

	int compareTo(Object v) {
		if (v as Version) {
			return compareTo(v as Version);
		}
		assert false, 'Only compare to Version is supported'
	}

	String toString() {
		return "Number: ${number1}.${number2}.${number3}"
	}
}

class UpdatePath implements Comparable {
	Version version
	File path

	int compareTo(UpdatePath up) {
		return version <=> up.version
	}

	int compareTo(Object v) {
		if (v as UpdatePath) {
			return compareTo((UpdatePath) v);
		}
		assert false, 'Only compare to UpdatePath is supported'
	}

	String toString() {
		return "Version: ${version}, path: ${path}"
	}
}

class SQLCommentsExtractor {

	String getComments(File file) {
		// find "/* xxx */" and "-- xxx" comments
		// .*? = suppress greedy matching
		// (?ms) multiline and dotall modes
		def matcher = file.text =~ /(?ms)(\/\*.*?\*\/)|(\-\-.*?$)/
		if (matcher) {
			return "Comments: \n" + matcher.collect {it[0]}.join("\n") + "\n"
		}
		return ""
	}
}

def version = new Version()
def numbers = versionStr.split("\\.")
version.number1 = numbers[0] as int
version.number2 = numbers[1] as int
version.number3 = numbers[2] as int

// find new updates
def paths = []
def dir = new File("update")
dir.eachFile { file ->
    if (file.directory) {
        return
    }
    def name = file.name
    if (! (name =~ /\d\d\d\d\d\d\d\d_\d+_\d+\.\d+\.\d+\.(sql|pls)/)) {
        println "Unsupported file ${file}"
        return
    }
    def sqlPos;
    if (name.endsWith(".sql")) {
        sqlPos = name.lastIndexOf(".sql");
    } else if (name.endsWith(".pls")) {
        sqlPos = name.lastIndexOf(".pls");
    }
    name = name.substring(0, sqlPos)

    def ver = new Version()

    numbers = name.substring(name.lastIndexOf('_') + 1).split('\\.')
    ver.number1 = numbers[0] as int
    ver.number2 = numbers[1] as int
    ver.number3 = numbers[2] as int

    if (version < ver) {
        paths.push new UpdatePath(version : ver, path : file)
    }
}

paths.sort()

SQLCommentsExtractor commentsExtractor = new SQLCommentsExtractor()
def result = ""

println "Updates for ${version}"
paths.eachWithIndex() { up, i ->
	println up.path
	print commentsExtractor.getComments(up.path)
    if (i + 1 < paths.size()) {
        result += up.path.path + ":"
    } else {
        result += up.path.path
    }
}

properties["result"] = result
