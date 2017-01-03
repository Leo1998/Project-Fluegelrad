import Foundation

enum SortingCategory: String {
	case rating = "Rating"
	case alphabetically = "Alphabetically"
	case chronologically = "Chronologically"
	case host = "Host"
	
	static let all = [rating, alphabetically, chronologically, host]
}
