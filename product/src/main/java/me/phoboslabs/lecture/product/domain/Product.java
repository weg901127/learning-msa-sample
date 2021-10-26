package me.phoboslabs.lecture.product.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import me.phoboslabs.lecture.adaptor.domain.entity.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;

@Slf4j
@Entity
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Product extends BaseEntity {

	@Id
	private Integer id;

	private String productNo;

	private String name;
	private Long price;
	private Integer stock;
	@Setter
	private Boolean soldOut = false;

	@Builder
	private Product(final Integer id, final String productNo, final String name, final long price, final int stock) {
		this.id = id;
		this.productNo = productNo;
		this.name = name;
		this.price = price;
		this.stock = stock;
	}

	public static Product create(final Integer id, final String productNo, final String name, final long price, final int stock) {
		return Product.builder()
				.id(id)
				.productNo(productNo)
				.name(name)
				.price(price)
				.stock(stock)
				.build();
	}

	public boolean isSoldOut() {
		return this.soldOut && this.stock <= 0;
	}

	public void updateSoldOut(final boolean isSoldOut) {
		this.soldOut = isSoldOut;
	}

	public void updateStock(final int decreaseStock) throws Exception {
		final int modifiedStock = this.stock - decreaseStock;
		if (modifiedStock <= 0) {
			this.soldOut = true;
			if (modifiedStock < 0) {
				this.stock = 0;
				log.error("재고수량이 마이너스 입니다. 확인이 필요합니다. productId: {}, stock: {}, decreaseStock: {}", this.id, this.stock, decreaseStock);
				throw new Exception("재고수량이 마이너스 입니다. 확인이 필요합니다. productId: " + this.id);
			} else {
				this.stock = modifiedStock;
			}
		}
	}
}
