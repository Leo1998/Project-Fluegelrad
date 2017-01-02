import Foundation

enum Filter: String {
	case name = "Name"
	case host = "Host"
	case age = "Age"
	case free = "Free"
	
	static let all = [name, host, age, free]
}
