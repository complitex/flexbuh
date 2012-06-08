import java.text.*

assert args.size() >= 1, 'Please specify version in YYYY-MM-DD[-N] format'

def versionStr = args[0]

assert versionStr =~ /\d\d\d\d-\d\d-\d\d(-\d+)?/, 'Need to specify version as parameter in YYYY-MM-DD[-N] format'

class Version implements Comparable {
	Date date
	int number

	int compareTo(Version v) {
		return date <=> v.date ?: number <=> v.number
	}

	int compareTo(Object v) {
		if (v as Version) {
			return compareTo(v as Version);
		}
		assert false, 'Only compare to Version is supported'
	}

	String toString() {
		return "Date: ${date.format('yyyy-MM-dd')}, number: ${number}"
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

def version = new Version(number : 0)
if (versionStr.count("-") == 3) {
	version.number = versionStr.substring("yyyy-MM-dd-".size()) as int
	versionStr = versionStr.substring(0, "yyyy-MM-dd".size())
}
version.date = new SimpleDateFormat("yyyy-MM-dd").parse(versionStr)

// find new updates
def paths = []
def dir = new File("update")
dir.eachFile { file ->
    if (file.directory) {
        return
    }
    def name = file.name
    if (! (name =~ /\d\d\d\d_\d\d_\d\d(_\d+)?\.(sql)|(pls)/)) {
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

    def ver = new Version(number : 0)
    if (name.count("_") == 3) {
        def num = name.substring('yyyy_MM_dd_'.size())

        ver.number = num as int
        name = name.substring(0, 'yyyy_MM_dd'.size())
    }
    ver.date = new SimpleDateFormat('yyyy_MM_dd').parse(name)

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
