"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { isAuthenticated } from "@/lib/auth"
import { Navigation } from "@/components/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet"
import { mockProducts, type Product, type Order } from "@/lib/mock-data"
import { ShoppingCart, Package, Plus, Minus, Trash2 } from "lucide-react"
import { useToast } from "@/hooks/use-toast"
import { getAuthToken } from "@/lib/auth"
import axios from "axios"

interface CartItem {
  product: Product
  quantity: number
}

const token = `Bearer ${getAuthToken()}` || "";
export default function ProductsPage() {
  //const token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTc2MTAwMDEyOSwiZXhwIjoxNzYxOTAwMTI5fQ.SHRhJEUOkNkuqU5azkFs7sQ7lC2RG5raJBroYBkZPLk"
  const router = useRouter()
  const { toast } = useToast()
  const [cart, setCart] = useState<CartItem[]>([])
  const [orders, setOrders] = useState<Order[]>([])
  const [userPoints, setUserPoints] = useState(0);

  useEffect(() => {
    if (!isAuthenticated()) {
      router.push("/login")
    } 
    readUserPoints();
  }, [])
  
  // 함수: 서버에서 포인트 받아오기
  const readUserPoints = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/v1/orders/points", {
        headers: { Authorization: token },
      });
      setUserPoints(response.data.data);
    }
    catch (err: any) {
      console.error(err);
      toast({
        title: "포인트 조회 실패",
        description: err.response?.data?.message || "서버 오류가 발생했습니다.",
        variant: "destructive",
      });
    }
  };

  // 함수: 장바구니에 아이템 추가
  const addToCart = (product: Product) => {
    const existingItem = cart.find((item) => item.product.id === product.id)

    if (existingItem) {
      setCart(cart.map((item) => (item.product.id === product.id ? { ...item, quantity: item.quantity + 1 } : item)))
    } else {
      setCart([...cart, { product, quantity: 1 }])
    }
    toast({title: "장바구니에 추가되었습니다", description: product.name,})
  }

  // 함수: 장바구니 속 아이템 숫자 변경
  const updateQuantity = (productId: string, delta: number) => {
    setCart(
      cart
        .map((item) => {
          if (item.product.id === productId) {
            const newQuantity = item.quantity + delta
            return newQuantity <= 0 ? null : { ...item, quantity: newQuantity }
          }
          return item
        })
        .filter(Boolean) as CartItem[],
    )
  }

  // 함수: 장바구니 속 아이템 숫자 변경 시, 0되면 장바구니에서 삭제
  const removeFromCart = (productId: string) => {
    setCart(cart.filter((item) => item.product.id !== productId))
  }

  // 함수: 장바구니 속 값 총합
  const getTotalPrice = () => {
    return cart.reduce((sum, item) => sum + item.product.price * item.quantity, 0)
  }

  // 함수: 장바구니에서 구매 누르면 처리
  const handleCheckout = async () => {
    const totalPrice = getTotalPrice()

    if (totalPrice > userPoints) {
      toast({title: "결제 실패", description: "포인트가 부족합니다.", variant: "destructive",})
      return
    }

    //서버로 보낼 주문 데이터 생성
    const orderData = {
      totalPrice,
      orderProducts: cart.map((item) => ({
        productId: item.product.id,
        quantity: item.quantity
      })),
    }

    try {
      //서버에 주문 데이터 전송
      const response = await axios.post("http://localhost:8080/api/v1/orders",
        orderData,
        {
          headers: {
            Authorization: token,
          },
        } 
      )

      // 프론트에 표시할 새 주문 내역 생성
      const newOrder: Order = {
        id: response.data.data,
        userId: "currentUser",
        items: cart.map((item) => ({
          productId: item.product.id, quantity: item.quantity,
          name: item.product.name, price: item.product.price,})),
        totalPrice, status: "ORDERED",
        date: new Date().toISOString().split("T")[0],
      }
      
      // 장바구니 속 아이템들을 하나의 주문으로 묶어 주문 내역으로 보내기
      //setOrders([newOrder, ...orders])
      await readOrders()
      // 유저의 포인트를 갱신해서 출력
      setUserPoints(userPoints - totalPrice)
      // 장바구니 비우기
      setCart([])

      toast({
        title: "결제 완료",
        description: `${totalPrice}P가 차감되었습니다.`,
      })
    } catch (err : any) {
    console.error(err)
      toast({
        title: "결제 실패",
        description: err.response?.data?.message || "서버 오류가 발생했습니다.",
        variant: "destructive",})
    }
  }

  // 함수: 주문 내역에 표시할 함수 표시
  const readOrders = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/v1/orders", {
        headers: {Authorization: token},
      })

      const apiResponse = response.data //ApiResponse 구조 용도
      const orderlist = apiResponse.data// 그 안에 있는 실제 List<OrderReadByIdResponse>
      
      // OrderReadByIdResponse DTO를 프론트의 Order 구조로 변환
      const orders: Order[] = orderlist.map((o : any) => ({
        id: String(o.orderId),
        userId: "currentuser",
        items: o.orderProducts.map((p : any) => ({
          productId: p.productName, //서버에서 product Id는 안 보냄
          name: p.productName,
          quantity: p.quantity,
          price: 0, //서버에 가격 정보 안 보내서 0으로 표시
        })),
        totalPrice: o.totalPrice,
        status: o.orderStatus,
        date: o.updatedAt?.split("T")[0] || "",
      }))

      // 후처리한 orders를 주문 내역 화면에 표시
      setOrders(orders)
    } catch (err: any) {
      console.error(err)
      toast({
        title: "주문 내역 조회 실패", variant: "destructive",
        description: err.response?.data?.message || "서버 오류가 발생했습니다."})
    }
  }

  // 함수: 주문 내역에서 주문 취소 시
  const cancelOrder = async (orderId: string) => {

    // 특정 오더 타게팅
    const order = orders.find((o) => o.id === orderId)
    if (!order) return
    // 특정 오더가 ORDERED 상태 아니면 예외처리
    if (order.status !== "ORDERED") {
      toast({title: "취소 불가", description: "이미 배송되었거나 취소된 주문입니다.", variant: "destructive",})
      return
    }

    // 주문 취소 요청
    try {
      const response = await axios.patch(`http://localhost:8080/api/v1/orders/${orderId}/cancel`, null, {
        headers: { Authorization: token },
      });

      await readOrders();
    } catch (err : any) {
      console.error("주문 취소 실패: ", err);
      toast({
        title: "주문 취소 실패", variant: "destructive",
        description: err.response?.data?.message || "서버 오류가 발생했습니다.",
      })
    }



    // 장바구니 속 아이템들을 하나의 주문으로 묶어 주문 내역으로 보내기
    //setOrders(orders.map((o) => (o.id === orderId ? { ...o, status: "CANCELED" as const } : o)))
    // 여기에 주문 취소 요청 로직 출력해야.
    // 유저 포인트를 갱신해서 출력 -> 위의 주문 취소 요청이 성공한 경우에만.
    setUserPoints(userPoints + order.totalPrice)

    toast({
      title: "주문 취소",
      description: `${order.totalPrice}P가 환불되었습니다.`,
    })
  }

  if (!isAuthenticated()) {
    return null
  }

  return (
    <div className="min-h-screen bg-background">
      <Navigation />

      <main className="container mx-auto px-4 py-8">
        <div className="mb-6 flex items-center justify-between">
          <h1 className="text-3xl font-bold text-balance">상품 구매</h1>
          <div className="flex items-center gap-4">
            <div className="text-right">
              <p className="text-sm text-muted-foreground">보유 포인트</p>
              <p className="text-2xl font-bold text-primary">{userPoints !== null ? `${userPoints}P` : "-"}</p>
            </div>
            <Sheet>
              <SheetTrigger asChild>
                <Button variant="outline" size="icon" className="relative bg-transparent">
                  <ShoppingCart className="h-5 w-5" />
                  {cart.length > 0 && (
                    <span className="absolute -right-1 -top-1 flex h-5 w-5 items-center justify-center rounded-full bg-primary text-xs text-primary-foreground">
                      {cart.length}
                    </span>
                  )}
                </Button>
              </SheetTrigger>
              <SheetContent>
                <SheetHeader>
                  <SheetTitle>장바구니</SheetTitle>
                </SheetHeader>
                <div className="mt-6 space-y-4">
                  {cart.length === 0 ? (
                    <p className="text-center text-muted-foreground">장바구니가 비어있습니다</p>
                  ) : (
                    <>
                      {cart.map((item) => (
                        <Card key={item.product.id}>
                          <CardContent className="p-4">
                            <div className="flex items-start justify-between">
                              <div className="flex-1">
                                <h3 className="font-semibold">{item.product.name}</h3>
                                <p className="text-sm text-primary">{item.product.price}P</p>
                              </div>
                              <Button variant="ghost" size="icon" onClick={() => removeFromCart(item.product.id)}>
                                <Trash2 className="h-4 w-4" />
                              </Button>
                            </div>
                            <div className="mt-2 flex items-center gap-2">
                              <Button variant="outline" size="icon" onClick={() => updateQuantity(item.product.id, -1)}>
                                <Minus className="h-4 w-4" />
                              </Button>
                              <span className="w-8 text-center">{item.quantity}</span>
                              <Button variant="outline" size="icon" onClick={() => updateQuantity(item.product.id, 1)}>
                                <Plus className="h-4 w-4" />
                              </Button>
                            </div>
                          </CardContent>
                        </Card>
                      ))}
                      <div className="border-t border-border pt-4">
                        <div className="mb-4 flex justify-between text-lg font-semibold">
                          <span>총 금액</span>
                          <span className="text-primary">{getTotalPrice()}P</span>
                        </div>
                        <Button onClick={handleCheckout} className="w-full">
                          결제하기
                        </Button>
                      </div>
                    </>
                  )}
                </div>
              </SheetContent>
            </Sheet>

            <Sheet>
              <SheetTrigger asChild>
                <Button variant="outline" size="icon">
                  <Package className="h-5 w-5" />
                </Button>
              </SheetTrigger>
              <SheetContent>
                <SheetHeader>
                  <SheetTitle>주문 내역</SheetTitle>
                </SheetHeader>
                <div className="mt-6 space-y-4">
                  {orders.length === 0 ? (
                    <p className="text-center text-muted-foreground">주문 내역이 없습니다</p>
                  ) : (
                    orders.map((order) => (
                      <Card key={order.id}>
                        <CardContent className="p-4">
                          <div className="mb-2 flex items-start justify-between">
                            <div>
                              <p className="text-sm text-muted-foreground">{order.date}</p>
                              <p className="font-semibold text-primary">{order.totalPrice}P</p>
                            </div>
                            <span
                              className={`rounded-full px-2 py-1 text-xs ${
                                order.status === "ORDERED"
                                  ? "bg-primary/10 text-primary"
                                  : order.status === "DELIVERED"
                                    ? "bg-secondary/10 text-secondary"
                                    : "bg-muted text-muted-foreground"
                              }`}
                            >
                              {order.status === "ORDERED"
                                ? "주문완료"
                                : order.status === "DELIVERED"
                                  ? "배송완료"
                                  : "취소됨"}
                            </span>
                          </div>
                          <div className="space-y-1 text-sm">
                            {order.items.map((item, idx) => (
                              <div key={idx} className="flex justify-between">
                                <span>{item.name}</span>
                                <span className="text-muted-foreground">x{item.quantity}</span>
                              </div>
                            ))}
                          </div>
                          {order.status === "ORDERED" && (
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => cancelOrder(order.id)}
                              className="mt-3 w-full"
                            >
                              주문 취소
                            </Button>
                          )}
                        </CardContent>
                      </Card>
                    ))
                  )}
                </div>
              </SheetContent>
            </Sheet>
          </div>
        </div>

        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {mockProducts.map((product) => (
            <Card key={product.id} className="overflow-hidden">
              <img src={product.image || "/placeholder.svg"} alt={product.name} className="h-48 w-full object-cover" />
              <CardHeader>
                <CardTitle className="text-balance">{product.name}</CardTitle>
                <CardDescription className="text-pretty">{product.description}</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="flex items-center justify-between">
                  <span className="text-2xl font-bold text-primary">{product.price}P</span>
                  <Button onClick={() => addToCart(product)}>장바구니</Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>
    </div>
  )
}
