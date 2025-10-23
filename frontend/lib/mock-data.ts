export interface Place {
  id: string
  name: string
  category: string
  address: string
  lat: number
  lng: number
  rating: number
  image: string
  description: string
  distance: number
}

export interface Review {
  id: string
  placeId: string
  userId: string
  userName: string
  content: string
  rating: number
  image?: string
  date: string
}

export interface Product {
  id: string
  name: string
  price: number
  description: string
  image: string
}

export interface Order {
  id: string
  userId: string
  items: { productId: string; quantity: number; name: string; price: number }[]
  totalPrice: number
  status: "ORDERED" | "DELIVERED" | "CANCELED"
  date: string
}

export interface Pet {
  id: string
  userId: string
  name: string
  gender?: string
  birthDate?: string
  breed?: string
}

export const mockPlaces: Place[] = [
  {
    id: "1",
    name: "행복동물병원",
    category: "병원",
    address: "서울시 강남구 테헤란로 123",
    lat: 37.5665,
    lng: 126.978,
    rating: 4.5,
    image: "/veterinary-clinic-exterior.png",
    description: "24시간 운영하는 동물병원입니다.",
    distance: 1.2,
  },
  {
    id: "2",
    name: "펫약국",
    category: "약국",
    address: "서울시 강남구 역삼동 456",
    lat: 37.5675,
    lng: 126.979,
    rating: 4.2,
    image: "/pet-pharmacy.jpg",
    description: "반려동물 전문 약국입니다.",
    distance: 2.3,
  },
  {
    id: "3",
    name: "멍멍식당",
    category: "식당",
    address: "서울시 강남구 삼성동 789",
    lat: 37.5685,
    lng: 126.98,
    rating: 4.8,
    image: "/pet-friendly-restaurant.jpg",
    description: "반려동물과 함께 식사할 수 있는 레스토랑입니다.",
    distance: 3.5,
  },
  {
    id: "4",
    name: "사랑동물병원",
    category: "병원",
    address: "서울시 서초구 서초대로 234",
    lat: 37.5695,
    lng: 126.981,
    rating: 4.6,
    image: "/animal-hospital.jpg",
    description: "전문 수의사가 상주하는 병원입니다.",
    distance: 4.1,
  },
  {
    id: "5",
    name: "펫케어약국",
    category: "약국",
    address: "서울시 서초구 반포대로 567",
    lat: 37.5705,
    lng: 126.982,
    rating: 4.3,
    image: "/pet-care-pharmacy.jpg",
    description: "반려동물 건강 관리 전문 약국입니다.",
    distance: 5.2,
  },
  {
    id: "6",
    name: "냥냥카페",
    category: "식당",
    address: "서울시 강남구 논현동 890",
    lat: 37.5715,
    lng: 126.983,
    rating: 4.7,
    image: "/cat-cafe.jpg",
    description: "고양이와 함께하는 카페입니다.",
    distance: 6.0,
  },
  {
    id: "7",
    name: "우리동물병원",
    category: "병원",
    address: "서울시 강남구 청담동 345",
    lat: 37.5725,
    lng: 126.984,
    rating: 4.4,
    image: "/veterinary-hospital.png",
    description: "친절한 진료로 유명한 동물병원입니다.",
    distance: 7.3,
  },
  {
    id: "8",
    name: "펫플러스약국",
    category: "약국",
    address: "서울시 강남구 대치동 678",
    lat: 37.5735,
    lng: 126.985,
    rating: 4.1,
    image: "/pet-pharmacy-store.jpg",
    description: "다양한 반려동물 의약품을 판매합니다.",
    distance: 8.5,
  },
  {
    id: "9",
    name: "펫프렌즈식당",
    category: "식당",
    address: "서울시 강남구 도곡동 901",
    lat: 37.5745,
    lng: 126.986,
    rating: 4.9,
    image: "/pet-friendly-dining.jpg",
    description: "반려동물 메뉴도 제공하는 식당입니다.",
    distance: 9.1,
  },
  {
    id: "10",
    name: "건강동물병원",
    category: "병원",
    address: "서울시 강남구 개포동 123",
    lat: 37.5755,
    lng: 126.987,
    rating: 4.5,
    image: "/healthy-pet-clinic.jpg",
    description: "예방접종 전문 동물병원입니다.",
    distance: 9.8,
  },
]

export const mockReviews: Review[] = [
  {
    id: "1",
    placeId: "1",
    userId: "user1",
    userName: "김철수",
    content: "선생님이 정말 친절하시고 우리 강아지를 잘 돌봐주셨어요. 24시간 운영이라 응급상황에도 안심이 됩니다.",
    rating: 5,
    image: "/happy-dog-at-vet.jpg",
    date: "2025-01-15",
  },
  {
    id: "2",
    placeId: "1",
    userId: "user2",
    userName: "이영희",
    content: "시설이 깨끗하고 대기시간도 짧았습니다. 진료비도 합리적이었어요.",
    rating: 4,
    date: "2025-01-14",
  },
  {
    id: "3",
    placeId: "2",
    userId: "user3",
    userName: "박민수",
    content: "필요한 약을 바로 구할 수 있어서 좋았습니다. 약사님이 복용법도 자세히 설명해주셨어요.",
    rating: 4,
    image: "/pet-medicine-variety.png",
    date: "2025-01-13",
  },
  {
    id: "4",
    placeId: "3",
    userId: "user4",
    userName: "최지은",
    content: "반려동물과 함께 식사할 수 있어서 너무 좋았어요. 음식도 맛있고 분위기도 좋습니다. 강력 추천합니다!",
    rating: 5,
    image: "/dog-at-restaurant.jpg",
    date: "2025-01-12",
  },
  {
    id: "5",
    placeId: "4",
    userId: "user5",
    userName: "정수진",
    content: "전문적인 진료를 받을 수 있었습니다. 수의사 선생님이 경험이 많으셔서 믿음이 갑니다.",
    rating: 5,
    date: "2025-01-11",
  },
  {
    id: "6",
    placeId: "5",
    userId: "user1",
    userName: "김철수",
    content: "약 종류가 다양하고 가격도 저렴합니다. 주차도 편리해요.",
    rating: 4,
    date: "2025-01-10",
  },
  {
    id: "7",
    placeId: "6",
    userId: "user2",
    userName: "이영희",
    content: "고양이들이 정말 귀엽고 카페 분위기도 아늑합니다. 커피도 맛있어요. 힐링하기 좋은 곳입니다.",
    rating: 5,
    image: "/cute-cats-in-cafe.jpg",
    date: "2025-01-09",
  },
  {
    id: "8",
    placeId: "7",
    userId: "user3",
    userName: "박민수",
    content: "친절하고 꼼꼼한 진료를 받았습니다. 다음에도 방문할 예정입니다.",
    rating: 4,
    date: "2025-01-08",
  },
  {
    id: "9",
    placeId: "8",
    userId: "user4",
    userName: "최지은",
    content: "약국이 깨끗하고 직원분들이 친절합니다. 필요한 약을 쉽게 찾을 수 있었어요.",
    rating: 4,
    date: "2025-01-07",
  },
  {
    id: "10",
    placeId: "9",
    userId: "user5",
    userName: "정수진",
    content:
      "반려동물 메뉴가 따로 있어서 좋았습니다. 우리 강아지가 정말 맛있게 먹었어요. 직원분들도 반려동물을 좋아하시는 게 느껴집니다.",
    rating: 5,
    image: "/dog-eating.png",
    date: "2025-01-06",
  },
]

export const mockProducts: Product[] = [
  {
    id: "1",
    name: "프리미엄 강아지 사료",
    price: 1,
    description: "영양가 높은 천연 재료로 만든 프리미엄 사료입니다.",
    image: "/premium-dog-food.png",
  },
  {
    id: "2",
    name: "고양이 장난감 세트",
    price: 80,
    description: "고양이가 좋아하는 다양한 장난감 세트입니다.",
    image: "/cat-toy-set.jpg",
  },
  {
    id: "3",
    name: "반려동물 목욕 용품",
    price: 120,
    description: "피부에 자극이 없는 천연 성분 목욕 용품입니다.",
    image: "/pet-bath-products.jpg",
  },
  {
    id: "4",
    name: "편안한 펫 침대",
    price: 200,
    description: "반려동물이 편안하게 쉴 수 있는 고급 침대입니다.",
    image: "/comfortable-pet-bed.png",
  },
]

export const mockUserReviews: Review[] = [
  {
    id: "ur1",
    placeId: "1",
    userId: "currentUser",
    userName: "나",
    content:
      "우리 강아지가 아플 때 정말 잘 치료해주셨어요. 선생님이 친절하시고 설명도 자세히 해주셔서 안심이 되었습니다.",
    rating: 5,
    image: "/dog-at-veterinary.jpg",
    date: "2025-01-10",
  },
  {
    id: "ur2",
    placeId: "3",
    userId: "currentUser",
    userName: "나",
    content:
      "반려동물과 함께 식사할 수 있는 곳을 찾다가 발견했어요. 음식도 맛있고 분위기도 좋아서 자주 방문하고 있습니다.",
    rating: 5,
    date: "2025-01-08",
  },
  {
    id: "ur3",
    placeId: "6",
    userId: "currentUser",
    userName: "나",
    content: "고양이 카페 중에서 가장 깨끗하고 고양이들도 건강해 보여요. 커피 맛도 좋고 힐링하기 좋은 공간입니다.",
    rating: 4,
    image: "/cats-in-cafe.jpg",
    date: "2025-01-05",
  },
  {
    id: "ur4",
    placeId: "9",
    userId: "currentUser",
    userName: "나",
    content:
      "반려동물 메뉴가 따로 있어서 정말 좋았어요. 우리 강아지가 맛있게 먹는 모습을 보니 행복했습니다. 다음에 또 올게요!",
    rating: 5,
    date: "2025-01-03",
  },
]

export const mockPets: Pet[] = [
  {
    id: "1",
    userId: "currentUser",
    name: "뽀삐",
    gender: "여",
    birthDate: "2020-05-15",
    breed: "말티즈",
  },
  {
    id: "2",
    userId: "currentUser",
    name: "초코",
    gender: "남",
    birthDate: "2019-08-20",
    breed: "푸들",
  },
]

export const mockBookmarks: Place[] = [
  {
    id: "1",
    name: "행복동물병원",
    category: "병원",
    address: "서울시 강남구 테헤란로 123",
    lat: 37.5665,
    lng: 126.978,
    rating: 4.5,
    image: "/veterinary-clinic-exterior.png",
    description: "24시간 운영하는 동물병원입니다.",
    distance: 1.2,
  },
  {
    id: "3",
    name: "멍멍식당",
    category: "식당",
    address: "서울시 강남구 삼성동 789",
    lat: 37.5685,
    lng: 126.98,
    rating: 4.8,
    image: "/pet-friendly-restaurant.jpg",
    description: "반려동물과 함께 식사할 수 있는 레스토랑입니다.",
    distance: 3.5,
  },
  {
    id: "6",
    name: "냥냥카페",
    category: "식당",
    address: "서울시 강남구 논현동 890",
    lat: 37.5715,
    lng: 126.983,
    rating: 4.7,
    image: "/cat-cafe.jpg",
    description: "고양이와 함께하는 카페입니다.",
    distance: 6.0,
  },
  {
    id: "4",
    name: "사랑동물병원",
    category: "병원",
    address: "서울시 서초구 서초대로 234",
    lat: 37.5695,
    lng: 126.981,
    rating: 4.6,
    image: "/animal-hospital.jpg",
    description: "전문 수의사가 상주하는 병원입니다.",
    distance: 4.1,
  },
  {
    id: "9",
    name: "펫프렌즈식당",
    category: "식당",
    address: "서울시 강남구 도곡동 901",
    lat: 37.5745,
    lng: 126.986,
    rating: 4.9,
    image: "/pet-friendly-dining.jpg",
    description: "반려동물 메뉴도 제공하는 식당입니다.",
    distance: 9.1,
  },
]
