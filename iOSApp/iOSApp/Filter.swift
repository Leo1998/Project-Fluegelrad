import Foundation

enum Filter: String {
	case name = "Name"
	case host = "Veranstalter"
	case age = "Alter"
	case free = "Kostenlos"
	
	static let all = [name, host, age, free]
}
