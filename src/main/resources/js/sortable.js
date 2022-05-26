/*
 * Symphony - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
class Sortable {
  constructor({
    parent,
    links         = document.querySelectorAll('[data-sjslink]'),
    active        = 'is-active',
    margin        = 20,
    responsive    = {
      980: {
        columns: 3
      },
      480: {
        columns: 2
      },
      0: {
        columns: 1
      }
    },
    fadeDuration  = {
      in: 300,
      out: 0
    }
  } = {}) {
    this.parent           = parent
    this.links            = Array.from(links)
    this.active           = active
    this.margin           = margin
    this.responsive       = responsive
    this.fadeDuration     = fadeDuration
    this.elements         = Array.from(this.parent.children)
    this.activeElements   = this.elements
    this.columns          = 1
    this.dataLink         = 'all'
    this.winWidth         = window.innerWidth

    this.init()
  }

  orderelements(){
    let {parent, activeElements, columns, blocWidth, responsive, margin} = this

    let arrayRectHeight   = activeElements.reduce((acc, el, id) => {
      let columnsHeight   = this._sumArrHeight(acc, columns)
      let positionX       = (id%columns) * (blocWidth + margin)
      let rectHeight      = (id - columns >= 0) ? (columnsHeight[id%columns] + (margin * Math.floor(id / columns))) : 0
      
      el.style.transform  = `translate3d(${positionX}px, ${rectHeight}px, 0)`

      acc.push(el.offsetHeight)
      return acc
    }, [])

    let columnsMaxHeight    = this._sumArrHeight(arrayRectHeight, columns)
    let parentHeight        = Math.max(...columnsMaxHeight) + (margin * (Math.floor(activeElements.length / columns) - 1))
    parent.style.height     = `${parentHeight}px`
  }

  handleFilterClick(ev, element){
    ev.preventDefault()
    let {links, active} = this

    if(element.dataset.sjslink === this.dataLink){
      return
    } else {
      this.dataLink = element.dataset.sjslink
      links.forEach(el => {
        el.isEqualNode(element) ? el.classList.add(active) : el.classList.remove(active)
      })
      this._filterElements(()=>{
        this.orderelements()
      })
    }
  }

  resize(){
    window.addEventListener('resize', () => {
      clearTimeout(window.sortableResize)
      window.sortableResize = setTimeout(() => {
        this.winWidth = window.innerWidth
        this._setBlocWidth(()=>{
          this.orderelements()
        })
      }, 500)
    })
  }

  init(){
    let {parent, links, active} = this

    links.forEach((el, id) => {
      if(id === 0){
        el.classList.add(active)
        this.dataLink = el.dataset.sjslink
      }
      el.addEventListener('click', ev => {
        this.handleFilterClick(ev, el)
      })
    })

    this._setBlocWidth()

    window.addEventListener('load', () => {
      this._filterElements(()=>{
        this.orderelements()
      })
      parent.style.opacity = 1
    })

    this.resize()
  }

  _setBlocWidth(callback){
    let {parent, elements, margin, responsive} = this

    let columns         = this.columns = this._columnsCount(responsive)['columns']
    let blocWidth       = this.blocWidth = (parent.clientWidth - (margin * (columns - 1))) / columns

    elements.forEach(el=>{
      el.style.width = `${blocWidth}px`
    })
    if(callback){
      callback()
    }
  }
  _filterElements(callback){
    let {elements, dataLink, fadeDuration} = this

    this.activeElements = elements.filter(el => {
      if(dataLink === 'all') {
        this._fadeIn(el, fadeDuration.in)
        return true
      } else {
        if(el.dataset.sjsel !== dataLink) {
          this._fadeOut(el, fadeDuration.out)
          return false
        } else {
          this._fadeIn(el, fadeDuration.in)
          return true
        }
      }
    })

    if(callback){
      callback()
    }
  }
  _sumArrHeight(arr, col){
    return arr.reduce((acc, val, id)=>{
      let cle = id%col
      if(!acc[cle]){
        acc[cle] = 0
      }
      acc[cle] = acc[cle]+val
      return acc 
    }, [])
  }
  _columnsCount(obj){
    let {winWidth} = this
    return Object.entries(obj).reduce((acc, val)=>{
      return winWidth > val[0] && val[0] >= Math.max(acc['width'])
        ? { width: val[0], columns: val[1]['columns'] }
        : acc
    }, {width: 0, columns: 4})
  }
  _fadeIn(el, duration = 300, callback){
    let opacity   = parseFloat(window.getComputedStyle(el, null).getPropertyValue("opacity")),
        interval  = 16,
        gap       = interval / duration
    
    el.style.display = 'block'

    function animation(){
      opacity += gap

      if(opacity <= 1){
        el.style.opacity = opacity
        requestAnimationFrame(animation)
      } else {
        el.style.opacity = 1
        if(callback){
          callback()
        }
      }
    }
    requestAnimationFrame(animation)
  }
  _fadeOut(el, duration = 300, callback){
    let opacity   = parseFloat(window.getComputedStyle(el, null).getPropertyValue("opacity")),
        interval  = 16,
        gap       = duration ? (interval / duration) : 1

    function animation(){
      opacity -= gap

      if(opacity >= 0){
        el.style.opacity = opacity
        requestAnimationFrame(animation)
      } else {
        el.style.opacity = 0
        el.style.display = 'none'
        if(callback){
          callback()
        }
      }
    }
    requestAnimationFrame(animation)
  }
}
HTMLElement.prototype.sortablejs = HTMLElement.prototype.sortablejs || function(params){
  return new Sortable({parent: this, ...params})
}
