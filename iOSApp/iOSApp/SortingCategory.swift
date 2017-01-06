import Foundation

enum SortingCategory: String {
	case rating = "Bewertung"
	case alphabetically = "Aphabetisch"
	case chronologically = "Chronologisch"
	case host = "Veranstalter"
	
	static let all = [rating, alphabetically, chronologically, host]
}
